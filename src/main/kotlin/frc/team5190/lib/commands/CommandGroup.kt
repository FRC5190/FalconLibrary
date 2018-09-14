package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.StatefulValueImpl
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.SendChannel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

open class CommandGroup(private val groupType: GroupType,
                        private val commands: List<Command>) : Command(commands.map { it.requiredSubsystems }.flatten()) {
    companion object {
        private val commandGroupContext = newFixedThreadPoolContext(2, "Command Group")
    }

    protected var parentCommandGroup: CommandGroup? = null
    private lateinit var commandGroupHandler: CommandGroupHandler
    private lateinit var tasksToRun: MutableSet<CommandGroupTask>
    private val commandGroupFinishCondition = CommandGroupFinishCondition()

    private inner class CommandGroupFinishCondition : StatefulValueImpl<Boolean>(false) {
        fun update() = changeValue(tasksToRun.isEmpty())
    }

    init {
        executeFrequency = 0

        commands.forEach { if (it is CommandGroup) it.parentCommandGroup = this }

        finishCondition += commandGroupFinishCondition
    }

    protected open fun createTasks(): List<CommandGroupTask> = commands.map { CommandGroupTask(it) }

    override suspend fun initialize() {
        commandGroupHandler = if (parentCommandGroup == null) ParentCommandGroupHandler() else ChildCommandGroupHandler()
        tasksToRun = createTasks().toMutableSet()
        commandGroupFinishCondition.update()
        (commandGroupHandler as? ParentCommandGroupHandler)?.start()
        synchronized<Unit>(tasksToRun) {
            if (groupType == GroupType.PARALLEL) {
                tasksToRun.forEach { commandGroupHandler.startTask(it, startTime) }
            } else {
                tasksToRun.firstOrNull()?.let { commandGroupHandler.startTask(it, startTime) }
            }
        }
    }

    override suspend fun dispose() {
        (commandGroupHandler as? ParentCommandGroupHandler)?.stop()
    }

    protected inner class CommandGroupTask(command: Command) : CommandTask(command, commandGroupHandler::handleFinish) {
        override fun stop(stopTime: Long) {
            synchronized(tasksToRun) { tasksToRun.remove(this) }
            if (groupType == GroupType.SEQUENTIAL) {
                tasksToRun.firstOrNull()?.let { commandGroupHandler.startTask(it, stopTime) }
            }
            commandGroupFinishCondition.update()
        }
    }

    private interface CommandGroupHandler {
        fun startTask(task: CommandGroupTask, startTime: Long)
        fun handleFinish(task: CommandTask, stopTime: Long)
    }

    private inner class ChildCommandGroupHandler : CommandGroupHandler by parentCommandGroup!!.commandGroupHandler

    private sealed class GroupEvent {
        class StartTaskEvent(val task: CommandGroupTask, val startTime: Long) : GroupEvent()
        class FinishTaskEvent(val task: CommandGroupTask, val stopTime: Long) : GroupEvent()
    }

    private inner class ParentCommandGroupHandler : CommandGroupHandler {

        private lateinit var groupActor: SendChannel<GroupEvent>
        private val actorFinishMutex = Mutex()
        private var destroyed = false

        private val allActiveTasks = mutableSetOf<CommandGroupTask>()
        private val queuedTasks = mutableSetOf<CommandGroupTask>()

        // Shortcut for checking if its a command group or not
        private val activeCommandTasks
            get() = allActiveTasks.filter { it.command !is CommandGroup }

        private suspend fun handleEvent(event: GroupEvent) {
            when (event) {
                is GroupEvent.StartTaskEvent -> {
                    val task = event.task

                    assert(!allActiveTasks.contains(task)) { "Task ${task.command::class.java.simpleName} already started" }
                    if (destroyed) {
                        println("[Command Group] The start of ${task.command::class.java.simpleName} was ignored since the command group is disposing.")
                        return
                    }
                    // Command Groups don't need the subsystem check
                    if (task.command !is CommandGroup) {
                        val used = activeCommandTasks.anyUsed(task.command.requiredSubsystems)
                        if (used) {
                            // Subsystems it needs is currently in use, queue it for later
                            println("[Command Group] Command ${task.command::class.java.simpleName} was delayed since it requires a subsystem currently in use")
                            queuedTasks.add(task)
                            return
                        }
                    }

                    // Command can run without any conflicts
                    allActiveTasks.add(task)
                    task.start0(event.startTime)
                }
                is GroupEvent.FinishTaskEvent -> {
                    val task = event.task
                    assert(allActiveTasks.contains(task)) { "Finish Task was called for ${task.command::class.java.simpleName} which isn't current running" }
                    // Command ended
                    allActiveTasks.remove(task)
                    task.stop0(event.stopTime)
                    // Check queue for any commands that can now run
                    queuedTasks.toSet().forEach { queuedTask ->
                        val used = activeCommandTasks.anyUsed(queuedTask.command.requiredSubsystems)
                        if (!used) {
                            // Command can now run without any conflicts
                            println("[Command Group] Resuming command ${task.command::class.java.simpleName} since it can now run")
                            queuedTasks.remove(queuedTask)
                            handleEvent(GroupEvent.StartTaskEvent(queuedTask, event.stopTime))
                        }
                    }
                }
            }
        }

        // Helper method for figuring out if a subsystem is used or not
        private fun List<CommandGroupTask>.anyUsed(subsystems: List<Subsystem>) =
                any { activeTask -> activeTask.command.requiredSubsystems.any { subsystems.contains(it) } }

        fun start() {
            destroyed = false
            groupActor = actor(commandGroupContext, Channel.UNLIMITED) {
                actorFinishMutex.withLock {
                    try {
                        for (event in channel) {
                            handleEvent(event)
                        }
                    } finally {
                        destroyed = true
                    }
                    // Stop currently running commands
                    allActiveTasks.forEach {
                        it.stop0(System.nanoTime())
                    }
                }
            }
        }

        suspend fun stop() {
            assert(!destroyed) { "Somehow the actor already got destroyed" }
            destroyed = true
            groupActor.close()
            actorFinishMutex.withLock { }
            allActiveTasks.clear()
            queuedTasks.clear()
        }

        override fun startTask(task: CommandGroupTask, startTime: Long) {
            if (destroyed) {
                println("[Command Group] Start of ${task.command::class.java.simpleName} was ignored since command group was destroyed")
                return
            }
            groupActor.sendBlocking(GroupEvent.StartTaskEvent(task, startTime))
        }

        override fun handleFinish(task: CommandTask, stopTime: Long) =
                groupActor.sendBlocking(GroupEvent.FinishTaskEvent(task as CommandGroupTask, stopTime))
    }

    enum class GroupType {
        PARALLEL,
        SEQUENTIAL
    }
}