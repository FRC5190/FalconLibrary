package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac34

open class SIExp3<A : SIValue<A>, B : SIValue<B>, C : SIValue<C>>(
        override val value: Double,
        internal val a: A,
        internal val b: B,
        internal val c: C
) : SIExpression<SIExp3<A, B, C>>() {

    override fun createNew(newValue: Double) = SIExp3(newValue, a, b, c)

    operator fun <D : SIUnit<D>> times(other: D) = SIExp4(value * other.value, a, b, c, other)

    @JvmName("divOA")
    operator fun div(other: A) = divA(other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    @JvmName("divOC")
    operator fun div(other: C) = divC(other)

    fun divA(other: A) = SIExp2(value / other.value, b, c)
    fun divB(other: B) = SIExp2(value / other.value, a, c)
    fun divC(other: C) = SIExp2(value / other.value, a, b)

    operator fun <BA : SIUnit<BA>, BB : SIUnit<BB>, BC : SIUnit<BC>, BD : SIUnit<BD>> div(other: SIExp4<BA, BB, BC, BD>) =
            SIFrac34(value / other.value, a, b, c, other.a, other.b, other.c, other.d)

    override fun toString() = "($a * $b * $c)"

    companion object {
        fun <A : SIValue<A>, B : SIValue<B>, C : SIValue<C>> create(a: A, b: B, c: C) =
                SIExp3(a.value * b.value * c.value, a, b, c)
    }
}