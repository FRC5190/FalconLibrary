package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newFixedThreadPoolContext
import org.ghrobotics.lib.utils.observabletype.ObservableVariable
import java.util.concurrent.ConcurrentLinkedDeque

open class CommandGroup(private val groupType: GroupType,
                        private val commands: List<Command>) : Command(commands.flatMap { it.requiredSubsystems }) {
    companion object {
        private val commandGroupContext = newFixedThreadPoolContext(2, "Command Group")
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

    protected open fun createTasks(): List<CommandGroupTask> = commands.map { createTask(it) }

    override suspend fun initialize() {
        commandGroupHandler = if (parentCommandGroup == null) ParentCommandGroupHandler() else ChildCommandGroupHandler()
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

    protected fun createTask(command: Command): CommandGroupTask {
        val handler = this.commandGroupHandler
        return object : CommandGroupTask(this, command, { task, stopTime ->
            handler.handleTaskFinish(task as CommandGroupTask, stopTime)
        }) {
            override fun stop(stopTime: Long) {
                synchronized(tasksToRun) { tasksToRun.remove(this) }
                if (groupType == GroupType.SEQUENTIAL) {
                    tasksToRun.firstOrNull()?.let { commandGroupHandler.queueTask(it, stopTime) }
                }
                commandGroupFinishCondition.update()
            }
        }
    }

    protected open class CommandGroupTask(val group: CommandGroup, command: Command, onFinish: (CommandTask, Long) -> Unit)
        : CommandTask(command, onFinish)

    private interface CommandGroupHandler {
        fun queueTask(task: CommandGroupTask, startTime: Long)
        fun handleTaskFinish(task: CommandGroupTask, stopTime: Long)
    }

    private inner class ChildCommandGroupHandler : CommandGroupHandler by parentCommandGroup!!.commandGroupHandler

    private inner class ParentCommandGroupHandler : CommandGroupHandler {

        private val tasksToStart = ConcurrentLinkedDeque<Pair<CommandGroupTask, Long>>()
        private val tasksToStop = ConcurrentLinkedDeque<Pair<CommandGroupTask, Long>>()

        private val activeTasks = mutableSetOf<CommandGroupTask>()
        private val delayedTasks = mutableSetOf<CommandGroupTask>()

        private var handlerJob: Job? = null
        private var isRunning = false

        fun start() {
            isRunning = true
            handlerJob = launch(commandGroupContext) {
                while (isRunning) {
                    startNewTasks()
                    stopOldTasks()
                    resumeDelayedTasks()
                }
            }
        }

        private suspend fun startTaskInternal(task: CommandGroupTask, startTime: Long) {
            activeTasks += task
            task.start0(startTime)
            //println("[Command Group] Adding ${task.command::class.java.simpleName}")
        }

        private suspend fun startNewTasks() {
            while (true) {
                val (task, startTime) = tasksToStart.poll() ?: return
                assert(!activeTasks.contains(task)) { "Task ${task.command::class.java.simpleName} already started" }

                if (task.command !is CommandGroup && !canStart(task.command.requiredSubsystems)) {
                    println("[Command Group] Command ${task.command::class.java.simpleName} was delayed since it requires a subsystem currently in use")
                    delayedTasks += task
                    return
                }

                startTaskInternal(task, startTime)
            }
        }

        private suspend fun stopOldTasks() {
            while (true) {
                val (task, stopTime) = tasksToStop.poll() ?: return
                assert(activeTasks.contains(task)) { "Finish Task was called for ${task.command::class.java.simpleName} which isn't current running" }
                stopTaskInternal(task, stopTime)
            }
        }

        private suspend fun stopTaskInternal(task: CommandGroupTask, stopTime: Long) {
            if (task.command is CommandGroup) {
                // Remove all sub commands of this command group since it finished
                activeTasks.filter { it.group == task.command }.forEach { stopTaskInternal(it, stopTime) }
                delayedTasks.removeIf { it.group == task.command }
            }
            //println("[Command Group] Removing ${task.command::class.java.simpleName}")
            // no need to stop it if its already stopped (this is used when disposing of sub commands)
            tasksToStop.removeIf { it.first == task }
            // stop the command since its not longer active
            activeTasks -= task
            task.stop0(stopTime)
        }

        private suspend fun resumeDelayedTasks() {
            val currentTime = System.nanoTime()

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
                activeTasks.none { task -> task.command !is CommandGroup && task.command.requiredSubsystems.any { neededSubsystems.contains(it) } }

        suspend fun stop() {
            isRunning = false
            handlerJob?.join()
            handlerJob = null

            tasksToStart.clear()
            tasksToStop.clear()
            delayedTasks.clear()
            val currentTime = System.nanoTime()
            for (task in activeTasks) {
                if (task.group == this@CommandGroup) tasksToStop += task to currentTime
            }
            stopOldTasks()
            assert(activeTasks.isEmpty()) { "Failed to dispose parent command group with leftover tasks" }
        }

        override fun queueTask(task: CommandGroupTask, startTime: Long) {
            //println("[Command Group] Requested Queue ${task.command::class.java.simpleName}")
            tasksToStart += task to startTime
        }

        override fun handleTaskFinish(task: CommandGroupTask, stopTime: Long) {
            //println("[Command Group] Requested Stop ${task.command::class.java.simpleName}")
            tasksToStop += task to stopTime
        }

    }

    enum class GroupType {
        PARALLEL,
        SEQUENTIAL
    }
}