package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.utils.observabletype.ObservableVariable

open class CommandGroup(
    private val groupType: GroupType,
    private val commands: List<Command>
) : Command(commands.flatMap { it.requiredSubsystems }) {
    companion object {
        private val commandGroupScope = CoroutineScope(newFixedThreadPoolContext(2, "Command Group"))
    }

    protected var parentCommandGroup: CommandGroup? = null
    private lateinit var commandGroupHandler: CommandGroupHandler
    private lateinit var tasksToRun: MutableSet<CommandGroupTask>
    private val commandGroupFinishCondition = CommandGroupFinishCondition()

    private inner class CommandGroupFinishCondition {
        val groupDone = ObservableVariable(false)
        fun update() {
            groupDone.value = tasksToRun.isEmpty()
        }
    }

    init {
        executeFrequency = 0

        commands.forEach { if (it is CommandGroup) it.parentCommandGroup = this }

        _finishCondition += commandGroupFinishCondition.groupDone
    }

    protected open fun createTasks(): List<CommandGroupTask> = commands.map { CommandGroupTask(it) }

    override suspend fun initialize() {
        commandGroupHandler = parentCommandGroup?.let { ChildCommandGroupHandler(it.commandGroupHandler) } ?:
                ParentCommandGroupHandler()
        tasksToRun = createTasks().toMutableSet()
        commandGroupFinishCondition.update()
        (commandGroupHandler as? ParentCommandGroupHandler)?.start()
        synchronized<Unit>(tasksToRun) {
            if (groupType == GroupType.PARALLEL) {
                tasksToRun.forEach { commandGroupHandler.queueTask(it, startTime) }
            } else {
                tasksToRun.firstOrNull()?.let { commandGroupHandler.queueTask(it, startTime) }
            }
        }
    }

    override suspend fun dispose() {
        (commandGroupHandler as? ParentCommandGroupHandler)?.stop()
    }

    protected inner class CommandGroupTask(
        command: Command
    ) : CommandTask(command, { task, stopTime ->
        commandGroupHandler.handleTaskFinish(task as CommandGroupTask, stopTime)
    }) {
        val group: CommandGroup = this@CommandGroup
        override fun stop(stopTime: Time) {
            synchronized(tasksToRun) { tasksToRun.remove(this) }
            if (groupType == GroupType.SEQUENTIAL) {
                tasksToRun.firstOrNull()?.let { commandGroupHandler.queueTask(it, stopTime) }
            }
            commandGroupFinishCondition.update()
        }
    }

    private interface CommandGroupHandler {
        fun queueTask(task: CommandGroupTask, startTime: Time)
        fun handleTaskFinish(task: CommandGroupTask, stopTime: Time)
    }

    private class ChildCommandGroupHandler(parent: CommandGroupHandler) : CommandGroupHandler by parent

    private class ParentCommandGroupHandler : CommandGroupHandler {

        private val taskChannel = Channel<Triple<Boolean, CommandGroupTask, Time>>(Channel.UNLIMITED)

        private val activeTasks = mutableSetOf<CommandGroupTask>()
        private val delayedTasks = mutableSetOf<CommandGroupTask>()

        private var handlerJob: Job? = null

        fun start() {
            handlerJob = commandGroupScope.launch {
                for ((start, task, time) in taskChannel) {
                    if (start) {
                        startNewTask(task, time)
                    } else {
                        stopOldTask(task, time)
                    }
                    resumeDelayedTasks()
                }
            }
        }

        private suspend fun startNewTask(task: CommandGroupTask, startTime: Time) {
            assert(!activeTasks.contains(task)) { "Task ${task.command::class.java.simpleName} already started" }

            if (task.command !is CommandGroup && !canStart(task.command.requiredSubsystems)) {
                println("[Command Group] Command ${task.command::class.java.simpleName} was delayed since it requires a subsystem currently in use")
                delayedTasks += task
                return
            }

            startTaskInternal(task, startTime)
        }

        private suspend fun stopOldTask(task: CommandGroupTask, stopTime: Time) {
            if (!activeTasks.contains(task)) {
                println("[Command Group] [Warning] Finish Task was called for ${task.command::class.java.simpleName} which isn't current running")
                return
            }
            stopTaskInternal(task, stopTime)
        }

        private suspend fun startTaskInternal(task: CommandGroupTask, startTime: Time) {
            activeTasks += task
            task.start0(startTime)
            //println("[Command Group] Adding ${task.command::class.java.simpleName}")
        }

        private suspend fun stopTaskInternal(task: CommandGroupTask, stopTime: Time) {
            if (task.command is CommandGroup) {
                // Remove all sub commands of this command group since it finished
                activeTasks.filter { it.group == task.command }.forEach { stopTaskInternal(it, stopTime) }
                delayedTasks.removeIf { it.group == task.command }
            }
            //println("[Command Group] Removing ${task.command::class.java.simpleName}")
            // stop the command since its not longer active
            activeTasks -= task
            task.stop0(stopTime)
        }

        private suspend fun resumeDelayedTasks() {
            val currentTime = System.nanoTime().nanosecond

            val iterator = delayedTasks.iterator()
            while (iterator.hasNext()) {
                val delayedTask = iterator.next()
                if (canStart(delayedTask.command.requiredSubsystems)) {
                    // Start the command since it can now run freely
                    iterator.remove()
                    startTaskInternal(delayedTask, currentTime)
                }
            }
        }

        private fun canStart(neededSubsystems: List<Subsystem>) =
            activeTasks.none { task ->
                task.command !is CommandGroup && task.command.requiredSubsystems.any {
                    neededSubsystems.contains(it)
                }
            }

        suspend fun stop() {
            taskChannel.close()
            handlerJob?.join()
            handlerJob = null

            delayedTasks.clear()
            val currentTime = System.nanoTime().nanosecond
            while (activeTasks.isNotEmpty()) {
                stopTaskInternal(activeTasks.first(), currentTime)
            }
            assert(activeTasks.isEmpty()) { "Failed to dispose parent command group with leftover tasks" }
        }

        override fun queueTask(task: CommandGroupTask, startTime: Time) {
            //println("[Command Group] Requested Queue ${task.command::class.java.simpleName}")
            taskChannel.sendBlocking(Triple(true, task, startTime))
        }

        override fun handleTaskFinish(task: CommandGroupTask, stopTime: Time) {
            //println("[Command Group] Requested Stop ${task.command::class.java.simpleName}")
            taskChannel.sendBlocking(Triple(false, task, stopTime))
        }

    }

    enum class GroupType {
        PARALLEL,
        SEQUENTIAL
    }
}