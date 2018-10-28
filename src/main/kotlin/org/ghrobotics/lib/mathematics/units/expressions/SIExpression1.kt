package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIUnit

open class SIExpression1<A : SIUnit<A, *>>(
        val a: A
) : SIExpression<SIExpression1<A>>() {
    override val asDouble: Double
        get() = a.asDouble
    override val asMetric by lazy { SIExpression1(a.asMetric) }
    override val absoluteValue by lazy { SIExpression1(a.absoluteValue) }

    override fun unaryMinus() = SIExpression1(-a)

    override fun times(other: Number) = SIExpression1(a * other)
    override fun div(other: Number) = SIExpression1(a / other)

    operator fun <C : SIUnit<C, *>> times(other: C) = SIExp2(a, other)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    fun divA(other: A): Double {
        val aM = a.asMetric.asDouble
        val oM = other.asMetric.asDouble

        return aM / oM
    }

    override fun toString() = buildString {
        append('(')
        append(a)
        append(')')
    }
}