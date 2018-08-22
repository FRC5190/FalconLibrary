package frc.team5190.lib.commands

import frc.team5190.lib.utils.*

enum class CommandState {
    /**
     * Command is ready and hasn't been ran yet
     */
    PREPARED,
    /**
     * Command is currently queued and waiting to be ran
     */
    QUEUED,
    /**
     * Command is currently running and hasn't finished
     */
    BAKING,
    /**
     * Command ended
     */
    BAKED
}

fun State<CommandState>.invokeWhenFinished(listener: StateListener<CommandState>) = invokeWhen(CommandState.BAKED, listener = listener)
fun State<CommandState>.invokeOnceWhenFinished(listener: StateListener<CommandState>) = invokeOnceWhen(CommandState.BAKED, listener = listener)

fun condition(command: Command): Condition = command.commandState.asFinishState()

fun State<CommandState>.asFinishState(): BooleanState = processedState(this) { it == CommandState.BAKED }