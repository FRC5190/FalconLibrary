package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue

class SIExp2<A : SIValue<A>, B : SIValue<B>>(
        override val value: Double,
        private val a: A,
        private val b: B
) : SIExpression<SIExp2<A, B>>() {
    override fun createNew(newValue: Double) = SIExp2(newValue, a, b)

    operator fun <C : SIUnit<C>> times(other: C) = SIExp3(value * other.value, a, b, other)

    @JvmName("divOA")
    operator fun div(other: A): B = divA(other)

    fun divA(other: A) = b * (a / other)

    @JvmName("divOB")
    operator fun div(other: B) = divB(other)

    fun divB(other: B) = a * (b / other)

    override fun toString() = "($a * $b)"

    companion object {
        fun <A : SIValue<A>, B : SIValue<B>> create(a: A, b: B) = SIExp2(a.value * b.value, a, b)
    }
}