package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue

class SIFrac11<T : SIValue<T>, B : SIValue<B>>(
        override val value: Double,
        internal val tA: T,
        internal val bA: B
) : SIFrac<SIFrac11<T, B>> {
    override fun createNew(newValue: Double) = SIFrac11(newValue, tA, bA)

    // OPERATORS

    @JvmName("timesEB")
    operator fun times(other: B) = timesB(other)

    @JvmName("divEO")
    operator fun <O : SIUnit<O>> div(other: O) = divO(other)

    fun timesB(o: B) = tA * (o / bA)
    fun <O : SIUnit<O>> divO(o: O) = SIFrac12(value / o.value, tA, bA, o)

    // DIVIDING BY FRACTIONS

    @JvmName("divEFBB")
    operator fun <O : SIUnit<O>> div(other: SIFrac12<T, B, O>) = other.bB.createNew(value / other.value)
}


