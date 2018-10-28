package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac32<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>>(
        top: SIExp3<TA, TB, TC>,
        bottom: SIExp2<BA, BB>
) : AbstractSIFrac<SIExp3<TA, TB, TC>, SIExp2<BA, BB>, SIFrac32<TA, TB, TC, BA, BB>>(
        top,
        bottom
), SIFracExpT3<TA, TB, TC>, SIFracExpB2<BA, BB> {
    override val tA: TA get() = top.a
    override val tB: TB get() = top.b
    override val tC: TC get() = top.c
    override val bA: BA get() = bottom.a
    override val bB: BB get() = bottom.b

    override fun create(newTop: SIExp3<TA, TB, TC>, newBottom: SIExp2<BA, BB>) = SIFrac32(newTop, newBottom)

    override fun div(other: SIFrac32<TA, TB, TC, BA, BB>) =
            ((tA / other.tA) * (tB / other.tB) * (tC / other.tC)) * ((other.bA / bA) * (other.bB / bB))

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    fun timesBA(other: BA) = SIFrac31(top * (other / bA), bB)
    fun timesBB(other: BB) = SIFrac31(top * (other / bB), bA)
}


