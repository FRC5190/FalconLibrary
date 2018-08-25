package frc.team5190.lib.utils

import kotlin.math.absoluteValue
import kotlin.math.sign

typealias DoubleSource = Source<Double>
typealias BooleanSource = Source<Boolean>

interface Source<T> {
    val value: T

    fun <O, P> withMerge(other: Source<O>, block: (T, O) -> P): Source<P> = of { block(this@Source.value, other.value) }

    fun <O> withEquals(other: O) = withEquals(of(other))
    fun <O> withEquals(other: Source<O>): BooleanSource = of { this@Source.value == other.value }

    fun <O> withProcessing(block: (T) -> O): Source<O> = of { block(this@Source.value) }

    companion object {
        fun <T> of(value: T): Source<T> = ConstantSource(value)
        fun <T> of(value: () -> T): Source<T> = VariableSource(value)
    }
}

private class ConstantSource<T>(override val value: T) : Source<T>
private class VariableSource<T>(val valueSource: () -> T) : Source<T> {
    override val value: T
        get() = valueSource()
}

@Suppress("FunctionName")
fun <T> Source(value: T) = Source.of(value)

@Suppress("FunctionName")
fun <T> Source(value: () -> T) = Source.of(value)

fun <T> BooleanSource.map(trueMap: T, falseMap: T) = map(Source(trueMap), Source(falseMap))
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: T) = map(trueMap, Source(falseMap))
fun <T> BooleanSource.map(trueMap: T, falseMap: Source<T>) = map(Source(trueMap), falseMap)
fun <T> BooleanSource.map(trueMap: Source<T>, falseMap: Source<T>) = withProcessing { if (it) trueMap.value else falseMap.value }

@Deprecated("", ReplaceWith("Source(value)"))
fun <T> constSource(value: T) = Source(value)

@Deprecated("", ReplaceWith("Source(value)"))
fun <T> variableSource(value: () -> T) = Source(value)

@Deprecated("", ReplaceWith("one.withMerge(two, value)"))
fun <T, K> mergeSource(one: Source<out T>, two: Source<out T>, value: (T, T) -> K) = one.withMerge(two, value)

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