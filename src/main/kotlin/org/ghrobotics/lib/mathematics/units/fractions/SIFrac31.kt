package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac31<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>>(
        top: SIExp3<TA, TB, TC>,
        bottom: BA
) : AbstractSIFrac<SIExp3<TA, TB, TC>, BA, SIFrac31<TA, TB, TC, BA>>(
        top,
        bottom
), SIFracExpT3<TA, TB, TC>, SIFracExpB1<BA> {
    override val tA: TA get() = top.a
    override val tB: TB get() = top.b
    override val tC: TC get() = top.c
    override val bA: BA get() = bottom

    override fun create(newTop: SIExp3<TA, TB, TC>, newBottom: BA) = SIFrac31(newTop, newBottom)

    override fun div(other: SIFrac31<TA, TB, TC, BA>) =
            ((tA / other.tA) * (tB / other.tB) * (tC / other.tC)) * ((other.bA / bA))

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    fun timesBA(other: BA) = top * (other / bA)
}


