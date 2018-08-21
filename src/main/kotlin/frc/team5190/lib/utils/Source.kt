package frc.team5190.lib.utils

import kotlin.math.absoluteValue
import kotlin.math.sign

typealias DoubleSource = Source<Double>
typealias BooleanSource = Source<Boolean>

interface Source<T> {
    val value: T
}

fun <T> constSource(value: T) = object : Source<T> {
    override val value = value
}

inline fun <T> variableSource(crossinline value: () -> T) = object : Source<T> {
    override val value: T
        get() = value()
}

inline fun <T, K> mergeSource(one: Source<out T>, two: Source<out T>, crossinline value: (T, T) -> K) = variableSource { value(one.value, two.value) }

fun <T> Source<T>.withEquals(equalsWhat: T): BooleanSource = withEquals(constSource(equalsWhat))
fun <T> Source<T>.withEquals(equalsWhat: Source<T>): BooleanSource = withProcessing { it == equalsWhat.value }

inline fun <F, T> Source<F>.withProcessing(crossinline processor: (F) -> T) = variableSource {
    processor(this@withProcessing.value)
}

fun <T> BooleanSource.map(trueMap: T, falseMap: T) = map(constSource(trueMap), constSource(falseMap))
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: T) = map(trueMap, constSource(falseMap))
fun <T> BooleanSource.map(trueMap: T, falseMap: Source<T>) = map(constSource(trueMap), falseMap)
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: Source<T>) = withProcessing { if (it) trueMap.value else falseMap.value }

fun DoubleSource.withThreshold(threshold: Double = 0.5): BooleanSource = withProcessing {
    val currentValue = this@withThreshold.value
    currentValue >= threshold
}

fun DoubleSource.withDeadband(deadband: Double, scaleDeadband: Boolean = true, maxMagnitude: Double = 1.0): DoubleSource = withProcessing {
    val currentValue = this@withDeadband.value
    if (currentValue in (-deadband)..deadband) return@withProcessing 0.0 // in deadband
    // outside deadband
    if (!scaleDeadband) return@withProcessing currentValue
    // scale so deadband is effective 0
    ((currentValue.absoluteValue - deadband) / (maxMagnitude - deadband)) * currentValue.sign
}