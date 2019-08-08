package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.lerp
import kotlin.math.absoluteValue

/**
 * @param value This is the value expressed in its SI Base Unit
 */
inline class SIUnit<K : SIKey>(val value: Double) : Comparable<SIUnit<K>> {

    val absoluteValue get() = SIUnit<K>(value.absoluteValue)

    operator fun unaryMinus() = SIUnit<K>(value.unaryMinus())

    operator fun plus(other: SIUnit<K>) = SIUnit<K>(value.plus(other.value))
    operator fun minus(other: SIUnit<K>) = SIUnit<K>(value.minus(other.value))

    operator fun times(other: Double) = SIUnit<K>(value.times(other))
    operator fun div(other: Double) = SIUnit<K>(value.div(other))

    operator fun times(other: Number) = times(other.toDouble())
    operator fun div(other: Number) = div(other.toDouble())

    override operator fun compareTo(other: SIUnit<K>): Int = value.compareTo(other.value)

    fun lerp(endValue: SIUnit<K>, t: Double) = SIUnit<K>(value.lerp(endValue.value, t))
    infix fun epsilonEquals(other: SIUnit<K>) = value.epsilonEquals(other.value)
}

interface SIKey

class Mult<N : SIKey, D : SIKey> : SIKey
class Frac<N : SIKey, D : SIKey> : SIKey

object Unitless : SIKey

val SIUnit<Unitless>.unitlessValue get() = value