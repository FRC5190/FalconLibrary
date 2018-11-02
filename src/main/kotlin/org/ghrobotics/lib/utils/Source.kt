package org.ghrobotics.lib.utils

import kotlin.math.absoluteValue
import kotlin.math.sign

typealias Source<T> = () -> T

typealias DoubleSource = Source<Double>
typealias BooleanSource = Source<Boolean>

fun <T, O, P> Source<T>.withMerge(
    other: Source<O>,
    block: (T, O) -> P
): Source<P> = { block(this@withMerge(), other()) }

fun <T, O> Source<T>.withEquals(other: O): BooleanSource = { this@withEquals() == other }
fun <T, O> Source<T>.withEquals(other: Source<O>): BooleanSource = { this@withEquals() == other() }

fun <T, O> Source<T>.map(block: (T) -> O): Source<O> = { block(this@map()) }

@Suppress("FunctionName")
fun <T> Source(value: T): Source<T> = { value }

@Suppress("FunctionName")
@Deprecated("Redundant", ReplaceWith("value"))
fun <T> Source(value: () -> T): Source<T> = value

fun DoubleSource.withThreshold(threshold: Double = 0.5): BooleanSource = map { this@withThreshold() >= threshold }

fun DoubleSource.withDeadband(
    deadband: Double,
    scaleDeadband: Boolean = true,
    maxMagnitude: Double = 1.0
): DoubleSource = map {
    val currentValue = this@withDeadband()
    if (currentValue in (-deadband)..deadband) return@map 0.0 // in deadband
    // outside deadband
    if (!scaleDeadband) return@map currentValue
    // scale so deadband is effective 0
    ((currentValue.absoluteValue - deadband) / (maxMagnitude - deadband)) * currentValue.sign
}

// Boolean Sources

fun <T> BooleanSource.map(trueMap: T, falseMap: T) = map(Source(trueMap), Source(falseMap))
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: T) = map(trueMap, Source(falseMap))
fun <T> BooleanSource.map(trueMap: T, falseMap: Source<T>) = map(Source(trueMap), falseMap)
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: Source<T>) = map { if (it) trueMap() else falseMap() }

operator fun BooleanSource.not() = map { !it }
infix fun BooleanSource.and(other: BooleanSource) = withMerge(other) { one, two -> one and two }
infix fun BooleanSource.or(other: BooleanSource) = withMerge(other) { one, two -> one or two }
infix fun BooleanSource.xor(other: BooleanSource) = withMerge(other) { one, two -> one or two }

// Comparable Sources

fun <T> Source<Comparable<T>>.compareTo(other: T) = map { it.compareTo(other) }
fun <T> Source<Comparable<T>>.compareTo(other: Source<T>) = withMerge(other) { one, two -> one.compareTo(two) }

fun <T> Source<Comparable<T>>.greaterThan(other: T) = compareTo(other).map { it > 0 }
fun <T> Source<Comparable<T>>.equalTo(other: T) = compareTo(other).map { it == 0 }
fun <T> Source<Comparable<T>>.lessThan(other: T) = compareTo(other).map { it < 0 }
fun <T> Source<Comparable<T>>.greaterThan(other: Source<T>) = compareTo(other).map { it > 0 }
fun <T> Source<Comparable<T>>.equalTo(other: Source<T>) = compareTo(other).map { it == 0 }
fun <T> Source<Comparable<T>>.lessThan(other: Source<T>) = compareTo(other).map { it < 0 }

@JvmName("reversedGreaterThan")
fun <T> Source<T>.greaterThan(other: Source<Comparable<T>>) = other.compareTo(this).map { it < 0 }

@JvmName("reversedEqualTo")
fun <T> Source<T>.equalTo(other: Source<Comparable<T>>) = other.compareTo(this).map { it == 0 }

@JvmName("reversedLessThan")
fun <T> Source<T>.lessThan(other: Source<Comparable<T>>) = other.compareTo(this).map { it > 0 }
