package frc.team5190.lib.commands

import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.actor
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.newFixedThreadPoolContext

object CommandHandler {

    private val commandContext = newFixedThreadPoolContext(2, "Command Context")

    /**
     * Stores the currently running tasks
     */
    private val tasks = mutableListOf<CommandTask>()

    private sealed class CommandEvent {
        class StartEvent(val command: Command, val startTime: Long, val callback: CompletableDeferred<Unit>?) : CommandEvent()
        class StopCommandEvent(val command: Command, val stopTime: Long) : CommandEvent()
        class StopEvent(val task: CommandTask, val stopTime: Long, val shouldStartDefault: (Subsystem) -> Boolean) : CommandEvent()
    }

    private val commandActor = actor<CommandEvent>(context = commandContext, capacity = Channel.UNLIMITED) {
        for (event in channel) {
            handleEvent(event)
        }
    }

    private suspend fun handleEvent(event: CommandEvent) {
        when (event) {
            is CommandEvent.StartEvent -> {
                val command = event.command
                val subsystems = command.requiredSubsystems
                // Free up required subsystems so we can start the task
                val commandsToStop = tasks.filter { task -> task.command.requiredSubsystems.any { subsystem -> subsystems.contains(subsystem) } }.toSet()
                // Stop the tasks that require the subsystems we need and start default commands for subsystems we don't need
                commandsToStop.forEach { task -> handleEvent(CommandEvent.StopEvent(task, event.startTime) { subsystem -> !subsystems.contains(subsystem) }) }
                // Start the task
                val task = CommandTask(command, ::stop)
                tasks.add(task)
                task.start0(event.startTime)
                event.callback?.complete(Unit)
            }
            is CommandEvent.StopCommandEvent -> {
                val task = tasks.find { it.command == event.command } ?: return
                handleEvent(CommandEvent.StopEvent(task, event.stopTime) { true })
            }
            is CommandEvent.StopEvent -> {
                val task = event.task
                if (!tasks.contains(task)) {
                    //println("[Command Handler] tried to stop ${task.command::class.java.simpleName} which isn't currently running!")
                    return
                }
                // Stop and dispose task
                task.stop0(event.stopTime)
                tasks -= task
                // Start default commands
                val defaultCommandsToStart = task.command.requiredSubsystems
                        .filter { event.shouldStartDefault(it) }
                        .mapNotNull { it.defaultCommand }
                defaultCommandsToStart.forEach { default -> handleEvent(CommandEvent.StartEvent(default, event.stopTime, null)) }
            }
        }
    }

    fun start(command: Command, startTime: Long): Deferred<Unit> {
        // Check if all subsystems are registered
        for (subsystem in command.requiredSubsystems) {
            if (!SubsystemHandler.isRegistered(subsystem)) throw IllegalArgumentException("A task required a subsystem that hasnt been registered! Subsystem: ${subsystem.name} ${subsystem::class.java.simpleName} Command: ${command::class.java.simpleName}")
        }
        val callback = CompletableDeferred<Unit>()
        commandActor.sendBlocking(CommandEvent.StartEvent(command, startTime, callback))
        return callback
    }

    fun stop(command: Command, stopTime: Long) = commandActor.sendBlocking(CommandEvent.StopCommandEvent(command, stopTime))
    private fun stop(task: CommandTask, stopTime: Long) = commandActor.sendBlocking(CommandEvent.StopEvent(task, stopTime) { true })
  
}