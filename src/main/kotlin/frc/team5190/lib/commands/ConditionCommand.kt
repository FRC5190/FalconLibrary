package frc.team5190.lib.commands

import frc.team5190.lib.utils.*

// Condition implementations for State<Boolean>

open class ConditionCommand(condition: Condition) : Command() {
    init {
        finishCondition += condition
    }
}

typealias Condition = BooleanState

fun condition(value: Boolean): Condition = condition { value }
fun condition(frequency: Int = 50, block: () -> Boolean): Condition = updatableState(frequency, block)

infix fun Condition.or(block: () -> Boolean) = this or condition(block = block)
infix fun Condition.and(block: () -> Boolean) = this and condition(block = block)

infix fun Condition.or(command: Command) = this or condition(command)
infix fun Condition.and(command: Command) = this and condition(command)

infix fun Condition.or(condition: Condition) = conditionGroup(this, condition) { one, two -> one || two }
infix fun Condition.and(condition: Condition) = conditionGroup(this, condition) { one, two -> one && two }

private fun conditionGroup(firstCondition: Condition, secondCondition: Condition, condition: (Boolean, Boolean) -> Boolean) = processedState(listOf(firstCondition, secondCondition)) { values ->
    val one = values[0]
    val two = values[1]
    condition(one, two)
}
