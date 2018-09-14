package frc.team5190.lib.commands

import frc.team5190.lib.utils.observabletype.ObservableValue
import frc.team5190.lib.utils.observabletype.and
import frc.team5190.lib.utils.observabletype.or

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

fun Command.asObservable(): ObservableValue<Boolean> = commandState.asObservableFinish()
fun ObservableValue<CommandState>.asObservableFinish(): ObservableValue<Boolean> = withProcessing { it == CommandState.BAKED }

infix fun ObservableValue<Boolean>.or(command: Command) = this or command.asObservable()
infix fun ObservableValue<Boolean>.and(command: Command) = this and command.asObservable()