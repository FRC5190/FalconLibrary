package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac31<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>>(
            override val value: Double,
            internal val tA: TA,
            internal val tB: TB,
            internal val tC: TC,
            internal val bA: BA
        ) : SIFrac<SIFrac31<TA, TB, TC, BA>> {
    override fun createNew(newValue: Double) = SIFrac31(newValue, tA, tB, tC, bA)

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    fun timesBA(other: BA) = SIExp3(value * other.value, tA, tB, tC)
}