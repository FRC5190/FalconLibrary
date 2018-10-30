package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue

class SIFrac12<T : SIValue<T>, BA : SIValue<BA>, BB : SIValue<BB>>(
        override val value: Double,
        internal val tA: T,
        internal val bA: BA,
        internal val bB: BB
) : SIFrac<SIFrac12<T, BA, BB>> {
    override fun createNew(newValue: Double) = SIFrac12(newValue, tA, bA, bB)

    // OPERATORS

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun div(other: BB) = timesBB(other)

    @JvmName("divEO")
    operator fun <O : SIValue<O>> div(other: O) = divO(other)

    // IMPLEMENTATIONS

    fun timesBA(o: BA) = SIFrac11(value * o.value, tA, bB)
    fun timesBB(o: BB) = SIFrac11(value * o.value, tA, bA)
    fun <O : SIValue<O>> divO(o: O) = SIFrac13(value / o.value, tA, bA, bB, o)
}


