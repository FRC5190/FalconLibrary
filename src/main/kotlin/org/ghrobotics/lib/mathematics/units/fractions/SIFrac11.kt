package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIPrefix
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2

fun <T : SIUnit<T, TE>, TE : Enum<TE>, B : SIUnit<B, BE>, BE : Enum<BE>> SIFrac11<T, B>.adjust(
    topPrefix: SIPrefix, topUnit: TE,
    bottomPrefix: SIPrefix, bottomUnit: BE
) = SIFrac11(
    top.convertTo(topPrefix, topUnit),
    bottom.convertTo(bottomPrefix, bottomUnit)
)

fun <T : SIValue<T>, B : SIUnit<B, BE>, BE : Enum<BE>> SIFrac11<T, B>.adjustBottom(
    bottomPrefix: SIPrefix, bottomUnit: BE
) = SIFrac11(top, bottom.convertTo(bottomPrefix, bottomUnit))

open class SIFrac11<T : SIValue<T>, B : SIValue<B>>(
    top: T,
    bottom: B
) : AbstractSIFrac<T, B, SIFrac11<T, B>>(
    top,
    bottom
), SIFracExpT1<T>, SIFracExpB1<B> {
    override val tA get() = top
    override val bA get() = bottom

    override fun create(newTop: T, newBottom: B) = SIFrac11(newTop, newBottom)

    override fun div(other: SIFrac11<T, B>) = (tA / other.tA) * (other.bA / bA)

    infix fun <O : SIUnit<O, *>> per(other: O) = div(other)

    // OPERATORS

    @JvmName("timesEB")
    operator fun times(other: B) = timesB(other)

    @JvmName("divEO")
    operator fun <O : SIUnit<O, *>> div(other: O) = divO(other)

    fun timesB(o: B) = tA * (o / bA)
    fun <O : SIUnit<O, *>> divO(o: O) = SIFrac12(top, SIExp2(bA, o))

    // DIVIDING BY FRACTIONS

    @JvmName("divEFBB")
    operator fun <O : SIUnit<O, *>> div(other: SIFrac12<T, B, O>) = other.bB * ((tA / other.tA) * (other.bA / bA))
}


