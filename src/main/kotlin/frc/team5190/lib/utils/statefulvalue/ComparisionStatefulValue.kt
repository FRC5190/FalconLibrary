package frc.team5190.lib.utils.statefulvalue

// This is used when the Stateful Value can do the comparison

fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThan(other: T): StatefulValue<Boolean> = greaterThan(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThan(other: T): StatefulValue<Boolean> = lessThan(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThanOrEquals(other: T): StatefulValue<Boolean> = greaterThanOrEquals(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThanOrEquals(other: T): StatefulValue<Boolean> = lessThanOrEquals(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.compareTo(other: T): StatefulValue<Int> = compareTo(StatefulValue(other))

fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThan(other: StatefulValue<T>): StatefulValue<Boolean> = greaterThan(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThan(other: StatefulValue<T>): StatefulValue<Boolean> = lessThan(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThanOrEquals(other: StatefulValue<T>): StatefulValue<Boolean> = greaterThanOrEquals(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThanOrEquals(other: StatefulValue<T>): StatefulValue<Boolean> = lessThanOrEquals(other, compareToFunction)
fun <F : Comparable<T>, T : Any> StatefulValue<F>.compareTo(other: StatefulValue<T>): StatefulValue<Int> = compareTo(other, compareToFunction)

private val <F : Comparable<T>, T> StatefulValue<F>.compareToFunction: (F, T) -> Int
    get() = { one, two -> one.compareTo(two) }