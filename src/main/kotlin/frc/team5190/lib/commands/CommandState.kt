package frc.team5190.lib.commands

import frc.team5190.lib.utils.BooleanState
import frc.team5190.lib.utils.State
import frc.team5190.lib.utils.StateListener
import frc.team5190.lib.utils.processedState

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

fun State<CommandState>.invokeWhenFinished(ignoreCurrent: Boolean = false, listener: StateListener<CommandState>) = invokeWhen(CommandState.BAKED, ignoreCurrent, listener)
fun State<CommandState>.invokeOnceWhenFinished(ignoreCurrent: Boolean = false, listener: StateListener<CommandState>) = invokeOnceWhen(CommandState.BAKED, ignoreCurrent, listener)

fun condition(command: Command): Condition = command.commandState.asFinishState()

fun State<CommandState>.asFinishState(): BooleanState = processedState(this) { it == CommandState.BAKED }