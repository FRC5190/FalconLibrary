package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIFrac33<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>>(
            override val value: Double,
            internal val tA: TA,
            internal val tB: TB,
            internal val tC: TC,
            internal val bA: BA,
            internal val bB: BB,
            internal val bC: BC
        ) : SIFrac<SIFrac33<TA, TB, TC, BA, BB, BC>> {
    override fun createNew(newValue: Double) = SIFrac33(newValue, tA, tB, tC, bA, bB, bC)

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    @JvmName("timesEBC")
    operator fun times(other: BC) = timesBC(other)

    fun timesBA(other: BA) = SIFrac32(value * other.value, tA, tB, tC, bB, bC)
    fun timesBB(other: BB) = SIFrac32(value * other.value, tA, tB, tC, bA, bC)
    fun timesBC(other: BC) = SIFrac32(value * other.value, tA, tB, tC, bA, bB)
}