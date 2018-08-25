package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.*

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

fun StatefulValue<CommandState>.asStatefulFinish(): StatefulBoolean = withProcessing { it == CommandState.BAKED }

@Suppress("FunctionName")
fun StatefulValue(command: Command) = command.commandStateValue.asStatefulFinish()

infix fun StatefulBoolean.or(command: Command): StatefulBoolean = this or StatefulValue(command)
infix fun StatefulBoolean.and(command: Command): StatefulBoolean = this and StatefulValue(command)

@Deprecated("", ReplaceWith("StatefulValue(command)", "frc.team5190.lib.utils.statefulvalue.StatefulValue"))
fun condition(command: Command) = StatefulValue(command)

@Deprecated("", ReplaceWith("asStatefulFinish()"))
fun StatefulValue<CommandState>.asFinishState(): StatefulBoolean = asStatefulFinish()