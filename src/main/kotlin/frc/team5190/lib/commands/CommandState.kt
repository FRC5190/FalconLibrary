package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.StatefulBoolean
import frc.team5190.lib.utils.statefulvalue.StatefulListener
import frc.team5190.lib.utils.statefulvalue.StatefulValue

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

fun StatefulValue<CommandState>.invokeWhenFinished(listener: StatefulListener<CommandState>) = invokeWhen(CommandState.BAKED, listener = listener)
fun StatefulValue<CommandState>.invokeOnceWhenFinished(listener: StatefulListener<CommandState>) = invokeOnceWhen(CommandState.BAKED, listener = listener)

fun condition(command: Command): Condition = command.commandState.asFinishState()

fun StatefulValue<CommandState>.asFinishState(): StatefulBoolean = withProcessing { it == CommandState.BAKED }