package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIExp5<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>, E : SIValue<E>>(
        override val value: Double,
        private val a: A,
        private val b: B,
        private val c: C,
        private val d: D,
        private val e: E
) : SIExpression<SIExp5<A, B, C, D, E>>() {
    override fun createNew(newValue: Double) = SIExp5(value, a, b, c, d, e)

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

    fun divA(other: A) = SIExp4(value / other.value, b, c, d, e)
    fun divB(other: B) = SIExp4(value / other.value, a, c, d, e)
    fun divC(other: C) = SIExp4(value / other.value, a, b, d, e)
    fun divD(other: D) = SIExp4(value / other.value, a, b, c, e)
    fun divE(other: E) = SIExp4(value / other.value, a, b, c, d)

    override fun toString() = "($a * $b * $c * $d * $e)"

    companion object {
        fun <A : SIValue<A>, B : SIValue<B>, C : SIValue<C>, D : SIValue<D>, E : SIValue<E>>
                create(a: A, b: B, c: C, d: D, e: E) =
                SIExp5(a.value * b.value * c.value * d.value * e.value, a, b, c, d, e)
    }
}