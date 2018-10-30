package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac32<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>>(
        override val value: Double,
        internal val tA: TA,
        internal val tB: TB,
        internal val tC: TC,
        internal val bA: BA,
        internal val bB: BB
) : SIFrac<SIFrac32<TA, TB, TC, BA, BB>>{
    override fun createNew(newValue: Double) = SIFrac32(newValue, tA, tB, tC, bA, bB)

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    fun timesBA(other: BA) = SIFrac31(value * other.value, tA, tB, tC, bB)
    fun timesBB(other: BB) = SIFrac31(value * other.value, tA, tB, tC, bA)
}


