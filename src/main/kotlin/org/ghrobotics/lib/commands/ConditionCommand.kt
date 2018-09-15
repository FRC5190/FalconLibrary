@file:Suppress("FunctionName")

package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableValue


fun ConditionCommand(condition: ObservableValue<Boolean>) = object : Command() {
    init {
        _finishCondition += condition
    }
}
