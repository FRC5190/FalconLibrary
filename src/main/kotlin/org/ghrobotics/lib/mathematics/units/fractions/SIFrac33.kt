package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

open class SIFrac33<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>>(
    top: SIExp3<TA, TB, TC>,
    bottom: SIExp3<BA, BB, BC>
) : AbstractSIFrac<SIExp3<TA, TB, TC>, SIExp3<BA, BB, BC>, SIFrac33<TA, TB, TC, BA, BB, BC>>(
    top,
    bottom
), SIFracExpT3<TA, TB, TC>, SIFracExpB3<BA, BB, BC> {
    override val tA: TA get() = top.a
    override val tB: TB get() = top.b
    override val tC: TC get() = top.c
    override val bA: BA get() = bottom.a
    override val bB: BB get() = bottom.b
    override val bC: BC get() = bottom.c

    override fun create(newTop: SIExp3<TA, TB, TC>, newBottom: SIExp3<BA, BB, BC>) = SIFrac33(newTop, newBottom)

    override fun div(other: SIFrac33<TA, TB, TC, BA, BB, BC>) =
        ((tA / other.tA) * (tB / other.tB) * (tC / other.tC)) * ((other.bA / bA) * (other.bB / bB) * (other.bC / bC))


    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)
    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)
    @JvmName("timesEBC")
    operator fun times(other: BC) = timesBC(other)

    fun timesBA(other: BA) = SIFrac32(top * (other / bA), SIExp2(bB, bC))
    fun timesBB(other: BB) = SIFrac32(top * (other / bB), SIExp2(bA, bC))
    fun timesBC(other: BC) = SIFrac32(top * (other / bC), SIExp2(bA, bB))
}


