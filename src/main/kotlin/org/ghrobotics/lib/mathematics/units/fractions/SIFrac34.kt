package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3
import org.ghrobotics.lib.mathematics.units.expressions.SIExp4
import org.ghrobotics.lib.mathematics.units.expressions.SIExp5

open class SIFrac34<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>, BD : SIValue<BD>>(
        top: SIExp3<TA, TB, TC>,
        bottom: SIExp4<BA, BB, BC, BD>
) : AbstractSIFrac<SIExp3<TA, TB, TC>, SIExp4<BA, BB, BC, BD>, SIFrac34<TA, TB, TC, BA, BB, BC, BD>>(
        top,
        bottom
), SIFracExpT3<TA, TB, TC>, SIFracExpB4<BA, BB, BC, BD> {
    override val tA: TA get() = top.a
    override val tB: TB get() = top.b
    override val tC: TC get() = top.c
    override val bA: BA get() = bottom.a
    override val bB: BB get() = bottom.b
    override val bC: BC get() = bottom.c
    override val bD: BD get() = bottom.d

    override fun create(newTop: SIExp3<TA, TB, TC>, newBottom: SIExp4<BA, BB, BC, BD>) = SIFrac34(newTop, newBottom)

    override fun div(other: SIFrac34<TA, TB, TC, BA, BB, BC, BD>) =
            ((tA / other.tA) * (tB / other.tB) * (tC / other.tC)) * ((other.bA / bA) * (other.bB / bB) * (other.bC / bC) * (other.bD / bD))

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun times(other: BB) = timesBB(other)

    @JvmName("timesEBC")
    operator fun times(other: BC) = timesBC(other)

    @JvmName("timesEBD")
    operator fun times(other: BD) = timesBD(other)

    fun timesBA(other: BA) = SIFrac33(top * (other / bA), SIExp3(bB, bC, bD))
    fun timesBB(other: BB) = SIFrac33(top * (other / bB), SIExp3(bA, bC, bD))
    fun timesBC(other: BC) = SIFrac33(top * (other / bC), SIExp3(bA, bB, bD))
    fun timesBD(other: BD) = SIFrac33(top * (other / bD), SIExp3(bA, bB, bC))

    @JvmName("divEFO")
    operator fun <O : SIValue<O>> div(other: O) = divFO(other)

    fun <O : SIValue<O>> divFO(other: O) = SIFrac35(top, SIExp5(bA, bB, bC, bD, other))
}


