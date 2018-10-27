package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.and
import org.ghrobotics.lib.utils.observabletype.map
import org.ghrobotics.lib.utils.observabletype.or

enum class CommandState {
    /**
     * FalconCommand is ready and hasn't been ran yet
     */
    PREPARED,
    /**
     * FalconCommand is currently running and hasn't finished
     */
    BAKING,
    /**
     * FalconCommand ended
     */
    BAKED
}

fun FalconCommand.asObservable(): ObservableValue<Boolean> = commandState.asObservableFinish()
fun ObservableValue<CommandState>.asObservableFinish(): ObservableValue<Boolean> = map { it == CommandState.BAKED }

infix fun ObservableValue<Boolean>.or(command: FalconCommand) = this or command.asObservable()
infix fun ObservableValue<Boolean>.and(command: FalconCommand) = this and command.asObservable()