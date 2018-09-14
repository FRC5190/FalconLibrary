@file:Suppress("FunctionName")

package frc.team5190.lib.commands

import frc.team5190.lib.utils.observabletype.ObservableValue


fun ConditionCommand(condition: ObservableValue<Boolean>) = object : Command() {
    init {
        _finishCondition += condition
    }
}
