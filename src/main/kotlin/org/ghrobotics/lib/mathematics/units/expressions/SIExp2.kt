package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp2<A : SIValue<A>, B : SIValue<B>>(
        val a: A,
        val b: B
) : SIExpression<SIExp2<A, B>>() {
    override val asDouble: Double
        get() = a.asDouble * b.asDouble
    override val asMetric by lazy { SIExp2(a.asMetric, b.asMetric) }
    override val absoluteValue by lazy { SIExp2(a.absoluteValue, b.absoluteValue) }

    override fun unaryMinus() = SIExp2(-a, b)

    override fun times(other: Number) = SIExp2(a * other, b)
    override fun div(other: Number) = SIExp2(a / other, b)

    operator fun <C : SIUnit<C, *>> times(other: C) = SIExp3(a, b, other)

    @JvmName("divOA")
    operator fun div(other: A): B = divA(other)

    fun divA(other: A) = b * (a / other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    fun divB(other: B) = a * (b / other)

    override fun toString() = buildString {
        append('(')
        append(a)
        append(" * ")
        append(b)
        append(')')
    }
}