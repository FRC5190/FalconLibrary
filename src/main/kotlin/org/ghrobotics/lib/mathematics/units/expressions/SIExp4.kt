package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp4<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>>(
    override val value: Double,
    internal val a: A,
    internal val b: B,
    internal val c: C,
    internal val d: D
) : SIExpression<SIExp4<A, B, C, D>>() {
    override fun createNew(newValue: Double) = SIExp4(newValue, a, b, c, d)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    @JvmName("divOC")
    operator fun div(other: C) = divC(other)

    @JvmName("divOD")
    operator fun div(other: D) = divD(other)

    fun divA(other: A) = SIExp3(value / other.value, b, c, d)
    fun divB(other: B) = SIExp3(value / other.value, a, c, d)
    fun divC(other: C) = SIExp3(value / other.value, a, b, d)
    fun divD(other: D) = SIExp3(value / other.value, a, b, c)

    override fun toString() = "($a * $b * $c * $d)"

    companion object {
        fun <A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>> create(a: A, b: B, c: C, d: D) =
                SIExp4(a.value * b.value * c.value * d.value, a, b, c, d)
    }
}