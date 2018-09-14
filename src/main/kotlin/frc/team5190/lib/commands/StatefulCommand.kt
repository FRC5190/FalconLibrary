@file:Suppress("FunctionName")

package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.*

@Deprecated("", ReplaceWith("StatefulBooleanCommand(condition)"))
fun ConditionCommand(condition: Condition) = StatefulBooleanCommand(condition)

fun <T> StatefulCommand(state: StatefulValue<T>, needed: T): Command = StatefulBooleanCommand(state.withProcessing { it == needed })
fun <T> StatefulCommand(state: StatefulValue<T>, process: (T) -> Boolean): Command = StatefulBooleanCommand(state.withProcessing(process))

fun StatefulBooleanCommand(state: StatefulBoolean, needed: Boolean = true): Command = object : Command() {
    init {
        finishCondition += state.withProcessing { it == needed }
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

