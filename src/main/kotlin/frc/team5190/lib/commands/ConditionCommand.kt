package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.*

// Condition implementations for State<Boolean>

open class ConditionCommand(condition: Condition) : Command() {
    init {
        finishCondition += condition
    }
}

@Deprecated("")
typealias Condition = StatefulBoolean

@Deprecated("", ReplaceWith("StatefulValue(value)", "frc.team5190.lib.utils.statefulvalue.StatefulValue"))
fun condition(value: Boolean): Condition = StatefulValue(value)
@Deprecated("", ReplaceWith("StatefulValue(frequency, block)", "frc.team5190.lib.utils.statefulvalue.StatefulValue"))
fun condition(frequency: Int = 50, block: () -> Boolean): Condition = StatefulValue(frequency, block)

@Deprecated("", ReplaceWith("this or StatefulValue(block)", "frc.team5190.lib.utils.statefulvalue.or", "frc.team5190.lib.utils.statefulvalue.StatefulValue"))
infix fun Condition.or(block: () -> Boolean) = this or StatefulValue(block)
@Deprecated("", ReplaceWith("this and StatefulValue(block)", "frc.team5190.lib.utils.statefulvalue.and", "frc.team5190.lib.utils.statefulvalue.StatefulValue"))
infix fun Condition.and(block: () -> Boolean) = this and StatefulValue(block)

infix fun Condition.or(command: Command) = this or condition(command)
infix fun Condition.and(command: Command) = this and condition(command)

