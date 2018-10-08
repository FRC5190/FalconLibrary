/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("MemberVisibilityCanBePrivate", "unused", "PropertyName")

package org.ghrobotics.lib.mathematics.units

import kotlin.math.absoluteValue

val Double.FT: Distance
    get() = Feet(this)
val Double.IN: Distance
    get() = Inch(this)
val Double.M: Distance
    get() = Meter(this)

val Float.FT: Distance
    get() = toDouble().FT
val Float.IN: Distance
    get() = toDouble().IN
val Float.M: Distance
    get() = toDouble().M

val Long.FT: Distance
    get() = toDouble().FT
val Long.IN: Distance
    get() = toDouble().IN
val Long.M: Distance
    get() = toDouble().M

val Int.FT: Distance
    get() = toDouble().FT
val Int.IN: Distance
    get() = toDouble().IN
val Int.M: Distance
    get() = toDouble().M

interface Distance : Comparable<Distance> {
    val asDouble: Double
    val asFloat: Float
    val asLong: Long
    val asInt: Int

    val FT: Distance
    val IN: Distance
    val M: Distance
    //val STU: Distance

    val absoluteValue: Distance

    operator fun plus(other: Distance): Distance
    operator fun minus(other: Distance): Distance = plus(-other)
    //operator fun times(other: Distance): Distance
    //operator fun div(other: Distance): Distance

    operator fun times(scalar: Int): Distance
    operator fun div(scalar: Int): Distance

    operator fun times(scalar: Double): Distance
    operator fun div(scalar: Double): Distance

    operator fun unaryPlus(): Distance = this
    operator fun unaryMinus(): Distance
}

internal abstract class DoubleDistance : Distance {
    abstract val value: Double

    override val asDouble: Double
        get() = value
    override val asFloat: Float
        get() = value.toFloat()
    override val asLong: Long
        get() = value.toLong()
    override val asInt: Int
        get() = value.toInt()

    override val absoluteValue by lazy { create(value.absoluteValue) }

    abstract fun create(newValue: Double): Distance
    abstract fun convertToNum(other: Distance): Double

    override fun plus(other: Distance) = create(convertToNum(other) + value)

    override fun times(scalar: Int) = create(value * scalar)
    override fun div(scalar: Int) = create(value / scalar)

    override fun times(scalar: Double) = create(value * scalar)
    override fun div(scalar: Double) = create(value / scalar)

    override fun unaryMinus() = create(-value)

    override fun compareTo(other: Distance) = value.compareTo(convertToNum(other))
}

internal abstract class LongDistance : Distance {
    abstract val value: Long

    override val asDouble: Double
        get() = value.toDouble()
    override val asFloat: Float
        get() = value.toFloat()
    override val asLong: Long
        get() = value
    override val asInt: Int
        get() = value.toInt()

    override val absoluteValue by lazy { create(value.absoluteValue) }

    abstract fun create(newValue: Long): Distance
    abstract fun convertToNum(other: Distance): Long

    override fun plus(other: Distance) = create(convertToNum(other) + value)

    override fun times(scalar: Int) = create(value * scalar)
    override fun div(scalar: Int) = create(value / scalar)

    override fun times(scalar: Double) = create(value * scalar.toLong())
    override fun div(scalar: Double) = create(value / scalar.toLong())

    override fun unaryMinus() = create(-value)

    override fun compareTo(other: Distance) = value.compareTo(convertToNum(other))
}

private class Feet(override val value: Double) : DoubleDistance() {
    override val FT = this
    override val IN by lazy { Inch(value * 12.0) }
    override val M by lazy { Meter(value / 3.28084) }

    override fun convertToNum(other: Distance) =
        (other.FT as DoubleDistance).value

    override fun create(newValue: Double) = Feet(newValue)
}

private class Inch(override val value: Double) : DoubleDistance() {
    override val FT by lazy { Feet(value / 12.0) }
    override val IN = this
    override val M by lazy { Meter(value / 12.0 / 3.28084) }

    override fun convertToNum(other: Distance) =
        (other.IN as DoubleDistance).value

    override fun create(newValue: Double) = Inch(newValue)
}

private class Meter(override val value: Double) : DoubleDistance() {
    override val FT by lazy { Feet(value * 3.28084) }
    override val IN by lazy { Inch(value * 3.28084 * 12.0) }
    override val M = this

    override fun convertToNum(other: Distance) =
        (other.M as DoubleDistance).value

    override fun create(newValue: Double) = Meter(newValue)
}
