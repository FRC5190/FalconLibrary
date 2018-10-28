package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp3<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>>(
        val a: A,
        val b: B,
        val c: C
) : SIExpression<SIExp3<A, B, C>>() {
    override val asDouble: Double
        get() = a.asDouble * b.asDouble * c.asDouble
    override val asMetric by lazy { SIExp3(a.asMetric, b.asMetric, c.asMetric) }
    override val absoluteValue by lazy { SIExp3(a.absoluteValue, b.absoluteValue, c.absoluteValue) }

    override fun unaryMinus() = SIExp3(-a, b, c)

    override fun times(other: Number) = SIExp3(a * other, b, c)
    override fun div(other: Number) = SIExp3(a / other, b, c)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    @JvmName("divOC")
    operator fun div(other: C) = divC(other)

    fun divA(other: A) = SIExp2(b * (a / other), c)
    fun divB(other: B) = SIExp2(a * (b / other), c)
    fun divC(other: C) = SIExp2(a * (c / other), b)

    override fun toString() = buildString {
        append('(')
        append(a)
        append(" * ")
        append(b)
        append(" * ")
        append(c)
        append(')')
    }
}