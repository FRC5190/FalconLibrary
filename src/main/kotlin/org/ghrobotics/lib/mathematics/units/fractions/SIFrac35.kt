package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIFrac35<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>, BD : SIValue<BD>, BE : SIValue<BE>>(
            override val value: Double,
            internal val tA: TA,
            internal val tB: TB,
            internal val tC: TC,
            internal val bA: BA,
            internal val bB: BB,
            internal val bC: BC,
            internal val bD: BD,
            internal val bE: BE
        ) : SIFrac<SIFrac35<TA, TB, TC, BA, BB, BC, BD, BE>> {
    override fun createNew(newValue: Double) = SIFrac35(newValue, tA, tB, tC, bA, bB, bC, bD, bE)

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    @JvmName("timesEBC")
    operator fun times(other: BC) = timesBC(other)

    @JvmName("timesEBD")
    operator fun times(other: BD) = timesBD(other)

    @JvmName("timesEBE")
    operator fun times(other: BE) = timesBE(other)

    fun timesBA(other: BA) = SIFrac34(value * other.value, tA, tB, tC, bB, bC, bD, bE)
    fun timesBB(other: BB) = SIFrac34(value * other.value, tA, tB, tC, bA, bC, bD, bE)
    fun timesBC(other: BC) = SIFrac34(value * other.value, tA, tB, tC, bA, bB, bD, bE)
    fun timesBD(other: BD) = SIFrac34(value * other.value, tA, tB, tC, bA, bB, bC, bE)
    fun timesBE(other: BE) = SIFrac34(value * other.value, tA, tB, tC, bA, bB, bC, bD)
}