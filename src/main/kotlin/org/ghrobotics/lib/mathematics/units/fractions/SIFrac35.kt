package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3
import org.ghrobotics.lib.mathematics.units.expressions.SIExp4
import org.ghrobotics.lib.mathematics.units.expressions.SIExp5

open class SIFrac35<TA : SIValue<TA>, TB : SIValue<TB>, TC : SIValue<TC>,
        BA : SIValue<BA>, BB : SIValue<BB>, BC : SIValue<BC>, BD : SIValue<BD>, BE : SIValue<BE>>(
        top: SIExp3<TA, TB, TC>,
        bottom: SIExp5<BA, BB, BC, BD, BE>
) : AbstractSIFrac<SIExp3<TA, TB, TC>, SIExp5<BA, BB, BC, BD, BE>, SIFrac35<TA, TB, TC, BA, BB, BC, BD, BE>>(
        top,
        bottom
), SIFracExpT3<TA, TB, TC>, SIFracExpB5<BA, BB, BC, BD, BE> {
    override val tA: TA get() = top.a
    override val tB: TB get() = top.b
    override val tC: TC get() = top.c
    override val bA: BA get() = bottom.a
    override val bB: BB get() = bottom.b
    override val bC: BC get() = bottom.c
    override val bD: BD get() = bottom.d
    override val bE: BE get() = bottom.e

    override fun create(newTop: SIExp3<TA, TB, TC>, newBottom: SIExp5<BA, BB, BC, BD, BE>) = SIFrac35(newTop, newBottom)

    override fun div(other: SIFrac35<TA, TB, TC, BA, BB, BC, BD, BE>) =
            ((tA / other.tA) * (tB / other.tB) * (tC / other.tC)) *
                    ((other.bA / bA) * (other.bB / bB) * (other.bC / bC) * (other.bD / bD) * (other.bE / bE))

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

    fun timesBA(other: BA) = SIFrac34(top * (other / bA), SIExp4(bB, bC, bD, bE))
    fun timesBB(other: BB) = SIFrac34(top * (other / bB), SIExp4(bA, bC, bD, bE))
    fun timesBC(other: BC) = SIFrac34(top * (other / bC), SIExp4(bA, bB, bD, bE))
    fun timesBD(other: BD) = SIFrac34(top * (other / bD), SIExp4(bA, bB, bC, bE))
    fun timesBE(other: BE) = SIFrac34(top * (other / bE), SIExp4(bA, bB, bC, bD))
}


