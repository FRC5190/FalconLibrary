package org.ghrobotics.lib.mathematics.units

interface SIValue<T> : Comparable<T> {
    val asDouble: Double
    val asFloat: Float
    val asLong: Long
    val asInt: Int

    val asMetric: T
    val absoluteValue: T

    operator fun unaryMinus(): T

    operator fun plus(other: T): T
    operator fun minus(other: T): T
    //operator fun times(other: T): T
    operator fun div(other: T): Double

    operator fun times(other: Number): T
    operator fun div(other: Number): T
}

abstract class AbstractSIValue<T> : SIValue<T> {
    override val asFloat
        get() = asDouble.toFloat()
    override val asInt: Int
        get() = asDouble.toInt()
    override val asLong: Long
        get() = asDouble.toLong()
}