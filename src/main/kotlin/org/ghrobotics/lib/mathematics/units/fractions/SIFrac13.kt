package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue

open class SIFrac13<T : SIValue<T>, BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>>(
        override val value: Double,
        internal val tA: T,
        internal val bA: BA,
        internal val bB: BB,
        internal val bC: BC
) : SIFrac<SIFrac13<T, BA, BB, BC>> {
    override fun createNew(newValue: Double) = SIFrac13(newValue, tA, bA, bB, bC)
}


