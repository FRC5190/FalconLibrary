package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp4<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>>(
    val a: A,
    val b: B,
    val c: C,
    val d: D
) : SIExpression<SIExp4<A, B, C, D>>() {
    override val asDouble: Double
        get() = a.asDouble * b.asDouble * c.asDouble * d.asDouble
    override val asMetric by lazy { SIExp4(a.asMetric, b.asMetric, c.asMetric, d.asMetric) }
    override val absoluteValue by lazy { SIExp4(a.absoluteValue, b.absoluteValue, c.absoluteValue, d.absoluteValue) }

    override fun unaryMinus() = SIExp4(-a, b, c, d)

    override fun times(other: Number) = SIExp4(a * other, b, c, d)
    override fun div(other: Number) = SIExp4(a / other, b, c, d)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    @JvmName("divOC")
    operator fun div(other: C) = divC(other)

    @JvmName("divOD")
    operator fun div(other: D) = divD(other)

    fun divA(other: A) = SIExp3(b * (a / other), c, d)
    fun divB(other: B) = SIExp3(a * (b / other), c, d)
    fun divC(other: C) = SIExp3(a * (c / other), b, d)
    fun divD(other: D) = SIExp3(a * (d / other), b, c)

    override fun toString() = "($a * $b * $c * $d)"
}