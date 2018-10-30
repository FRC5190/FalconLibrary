package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIFrac34<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>, BD : SIValue<BD>>(
        override val value: Double,
        internal val tA: TA,
        internal val tB: TB,
        internal val tC: TC,
        internal val bA: BA,
        internal val bB: BB,
        internal val bC: BC,
        internal val bD: BD
) : SIFrac<SIFrac34<TA, TB, TC, BA, BB, BC, BD>> {

    override fun createNew(newValue: Double) = SIFrac34(newValue, tA, tB, tC, bA, bB, bC, bD)

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    @JvmName("timesEBC")
    operator fun times(other: BC) = timesBC(other)

    @JvmName("timesEBD")
    operator fun times(other: BD) = timesBD(other)

    fun timesBA(other: BA) = SIFrac33(value * other.value, tA, tB, tC, bB, bC, bD)
    fun timesBB(other: BB) = SIFrac33(value * other.value, tA, tB, tC, bA, bC, bD)
    fun timesBC(other: BC) = SIFrac33(value * other.value, tA, tB, tC, bA, bB, bD)
    fun timesBD(other: BD) = SIFrac33(value * other.value, tA, tB, tC, bA, bB, bC)

    @JvmName("divEFO")
    operator fun <O : SIValue<O>> div(other: O) = divFO(other)

    fun <O : SIValue<O>> divFO(other: O) = SIFrac35(value / other.value, tA, tB, tC, bA, bB, bC, bD, other)
}


