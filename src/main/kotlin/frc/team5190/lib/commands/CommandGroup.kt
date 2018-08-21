package frc.team5190.lib.commands

import frc.team5190.lib.utils.StateImpl
import frc.team5190.lib.utils.State
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

abstract class CommandGroup(private val commands: List<Command>) : Command() {

    companion object {
        private val commandGroupContext = newFixedThreadPoolContext(2, "Command Group")
    }

    protected lateinit var commandTasks: List<GroupCommandTask>
    override val requiredSubsystems = commands.map { it.requiredSubsystems }.flatten()

    private var parentCommandGroup: CommandGroup? = null
    private lateinit var commandGroupHandler: CommandGroupHandler

    private inner class GroupCondition : StateImpl<Boolean>(false) {
        fun invoke() {
            internalValue = true
        }

        fun reset() {
            internalValue = false
        }
    }

    private val groupCondition = GroupCondition()

    init {
        updateFrequency = 0
        finishCondition += groupCondition
    }

    protected open fun initTasks() = commands.map { GroupCommandTask(this, it) }
    protected abstract suspend fun handleStartEvent()
    protected open suspend fun handleFinishEvent(stopTime: Long) {}

    override suspend fun initialize0() {
        super.initialize0()
        commandGroupHandler = if (parentCommandGroup != null) NestedCommandGroupHandler() else BaseCommandGroupHandler()
        groupCondition.reset()
        
        // Start this group

        commandTasks = initTasks()
        commandGroupHandler.start()
        handleStartEvent()
    }

    override suspend fun dispose0() {
        commandGroupHandler.dispose()
        super.dispose0()
    }

    protected suspend fun start(task: GroupCommandTask, startTime: Long) = commandGroupHandler.startCommand(task, startTime)

    protected inner class GroupCommandTask(val group: CommandGroup, command: Command) : CommandHandler.CommandTask(command) {
        override suspend fun stop(stopTime: Long) = commandGroupHandler.commandFinishCallback(this, stopTime)
    }

    private interface CommandGroupHandler {
        suspend fun start()
        suspend fun dispose()
        suspend fun commandFinishCallback(task: GroupCommandTask, stopTime: Long)
        suspend fun startCommand(task: GroupCommandTask, startTime: Long)
    }

    private inner class NestedCommandGroupHandler : CommandGroupHandler {
        private val parentHandler = parentCommandGroup!!.commandGroupHandler

        override suspend fun start() = Unit
        override suspend fun dispose() = Unit
        override suspend fun commandFinishCallback(task: GroupCommandTask, stopTime: Long) = parentHandler.commandFinishCallback(task, stopTime)
        override suspend fun startCommand(task: GroupCommandTask, startTime: Long) = parentHandler.startCommand(task, startTime)
    }

    private sealed class GroupEvent {
        class StartTask(val task: GroupCommandTask, val startTime: Long) : GroupEvent()
        class FinishTask(val task: GroupCommandTask, val stopTime: Long) : GroupEvent()
        object DestroyTask : GroupEvent()
    }

    private inner class BaseCommandGroupHandler : CommandGroupHandler {
        private lateinit var groupActor: SendChannel<GroupEvent>
        private val actorMutex = Mutex()

        private val activeCommands = mutableListOf<GroupCommandTask>()
        private val runningCommands = mutableListOf<GroupCommandTask>()
        private val queuedCommands = mutableListOf<GroupCommandTask>()

        private var destroyed = false

        override suspend fun start() {
            groupActor = actor(context = commandGroupContext, capacity = Channel.UNLIMITED) {
                actorMutex.withLock {
                    for (event in channel) {
                        if (destroyed && activeCommands.isEmpty()) return@withLock // exit since its done cleaning up
                        handleEvent(event)
                    }
                }
            }
        }

        private suspend fun handleEvent(event: GroupEvent) {
            //println("EVENT: ${event::class.java.simpleName}")
            when (event) {
                is GroupEvent.StartTask -> {
                    val task = event.task
                    if (runningCommands.contains(task)) {
                        println("[Command Group] Command ${task.command::class.java.simpleName} is already running, discarding...")
                        return
                    }
                    if (task.command is CommandGroup) {
                        runningCommands += task
                        task.command.parentCommandGroup = this@CommandGroup
                        task.initialize(event.startTime)
                        return
                    }
                    val canStart = canStart(task)
                    if (!canStart) {
                        queuedCommands += task
                        println("[Command Group] Command ${task.command::class.java.simpleName} was delayed since it requires a subsystem that already being used in the command group tree")
                        return
                    }
                    runningCommands += task
                    activeCommands += task
                    task.initialize(event.startTime)
                }
                is GroupEvent.FinishTask -> {
                    val task = event.task
                    if (!runningCommands.contains(task)) return // discard extra requests
                    runningCommands -= task
                    task.dispose()
                    task.group.commandTasks -= task
                    activeCommands -= task
                    task.group.parentCommandGroup = null
                    if (destroyed) {
                        closeIfFinished()
                        return // ignore
                    }
                    // Find queued commands that can run
                    var nextTask: GroupCommandTask?
                    do {
                        nextTask = queuedCommands.find { canStart(it) }
                        if (nextTask != null) {
                            queuedCommands -= nextTask
                            handleEvent(GroupEvent.StartTask(nextTask, event.stopTime))
                        }
                    } while (nextTask != null)
                    if (task.group.commandTasks.isEmpty()) {
                        task.group.groupCondition.invoke()
                        return // command group finished
                    }
                    task.group.handleFinishEvent(event.stopTime)
                }
                is GroupEvent.DestroyTask -> {
                    destroyed = true
                    // signal current tasks to dispose
                    activeCommands.forEach { it.stop(System.nanoTime()) }
                    closeIfFinished()
                }
            }
        }

        private fun closeIfFinished() {
            if (activeCommands.isEmpty()) groupActor.close() // close up
        }

        private fun canStart(task: GroupCommandTask): Boolean {
            val usedSubsystems = activeCommands.map { it.command.requiredSubsystems }.flatten()
            val neededSubsystems = task.command.requiredSubsystems
            return usedSubsystems.none { neededSubsystems.contains(it) }
        }

        override suspend fun dispose() {
            groupActor.send(GroupEvent.DestroyTask)
            actorMutex.withLock {
                assert(activeCommands.isEmpty()) { "Command Group failed to clean up" }
            }
            groupActor.close()
        }

        override suspend fun commandFinishCallback(task: GroupCommandTask, stopTime: Long) = groupActor.send(GroupEvent.FinishTask(task, stopTime))
        override suspend fun startCommand(task: GroupCommandTask, startTime: Long) = groupActor.send(GroupEvent.StartTask(task, startTime))
    }
}

open class ParallelCommandGroup(commands: List<Command>) : CommandGroup(commands) {
    override suspend fun handleStartEvent() {
        // Start all commands so they run in parallel
        val tasksToStart = commandTasks.toList()
        tasksToStart.forEach { start(it, startTime) }
    }
}

open class SequentialCommandGroup(commands: List<Command>) : CommandGroup(commands) {
    private lateinit var taskIterator: Iterator<GroupCommandTask>
    override suspend fun handleStartEvent() {
        taskIterator = commandTasks.iterator()
        startNextCommand(startTime) // Start only the first command
    }

    override suspend fun handleFinishEvent(stopTime: Long) = startNextCommand(stopTime) // Start next command

    private suspend fun startNextCommand(startTime: Long) {
        if (taskIterator.hasNext()) start(taskIterator.next(), startTime)
    }
}