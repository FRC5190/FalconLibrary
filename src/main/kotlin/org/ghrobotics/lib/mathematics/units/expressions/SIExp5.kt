package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp5<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>, E : SIValue<E>>(
        val a: A,
        val b: B,
        val c: C,
        val d: D,
        val e: E
) : SIExpression<SIExp5<A, B, C, D, E>>() {
    override val asDouble: Double
        get() = a.asDouble * b.asDouble * c.asDouble * d.asDouble * e.asDouble
    override val asMetric by lazy { SIExp5(a.asMetric, b.asMetric, c.asMetric, d.asMetric, e.asMetric) }
    override val absoluteValue by lazy {
        SIExp5(
                a.absoluteValue,
                b.absoluteValue,
                c.absoluteValue,
                d.absoluteValue,
                e.absoluteValue
        )
    }

    override fun unaryMinus() = SIExp5(-a, b, c, d, e)

    override fun times(other: Number) = SIExp5(a * other, b, c, d, e)
    override fun div(other: Number) = SIExp5(a / other, b, c, d, e)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    @JvmName("divOC")
    operator fun div(other: C) = divC(other)

    @JvmName("divOD")
    operator fun div(other: D) = divD(other)

    @JvmName("divOE")
    operator fun div(other: E) = divE(other)

    fun divA(other: A) = SIExp4(b * (a / other), c, d, e)
    fun divB(other: B) = SIExp4(a * (b / other), c, d, e)
    fun divC(other: C) = SIExp4(a * (c / other), b, d, e)
    fun divD(other: D) = SIExp4(a * (d / other), b, c, e)
    fun divE(other: E) = SIExp4(a * (e / other), b, c, d)

    override fun toString() = "($a * $b * $c * $d * $e)"
}