package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.SIPrefix
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.expressions.SIExp3

fun <T : SIUnit<T, TE>, TE : Enum<TE>,
        BA : SIUnit<BA, BAE>, BAE : Enum<BAE>,
        BB : SIUnit<BB, BBE>, BBE : Enum<BBE>> SIFrac12<T, BA, BB>.adjust(
    topPrefix: SIPrefix, topUnit: TE,
    bottomAPrefix: SIPrefix, bottomAUnit: BAE,
    bottomBPrefix: SIPrefix, bottomBUnit: BBE
) = SIFrac12(
    top.convertTo(topPrefix, topUnit),
    SIExp2(
        bottom.a.convertTo(bottomAPrefix, bottomAUnit),
        bottom.b.convertTo(bottomBPrefix, bottomBUnit)
    )
)

fun <T : SIValue<T>,
        BA : SIUnit<BA, BAE>, BAE : Enum<BAE>,
        BB : SIUnit<BB, BBE>, BBE : Enum<BBE>> SIFrac12<T, BA, BB>.adjustBottom(
    bottomAPrefix: SIPrefix, bottomAUnit: BAE,
    bottomBPrefix: SIPrefix, bottomBUnit: BBE
) = SIFrac12(
    top,
    SIExp2(
        bottom.a.convertTo(bottomAPrefix, bottomAUnit),
        bottom.b.convertTo(bottomBPrefix, bottomBUnit)
    )
)

open class SIFrac12<T : SIValue<T>, BA : SIValue<BA>, BB : SIValue<BB>>(
    top: T,
    bottom: SIExp2<BA, BB>
) : AbstractSIFrac<T, SIExp2<BA, BB>, SIFrac12<T, BA, BB>>(
    top,
    bottom
), SIFracExpT1<T>, SIFracExpB2<BA, BB> {
    override val tA get() = top
    override val bA get() = bottom.a
    override val bB get() = bottom.b

    override fun create(newTop: T, newBottom: SIExp2<BA, BB>) = SIFrac12(newTop, newBottom)

    override fun div(other: SIFrac12<T, BA, BB>) = ((tA / other.tA)) * ((other.bA / bA) * (other.bB / bB))

    infix fun <O : SIValue<O>> per(other: O) = div(other)

    // OPERATORS

    @JvmName("timesEBA")
    operator fun times(other: BA) = timesBA(other)

    @JvmName("timesEBB")
    operator fun div(other: BB) = timesBB(other)

    @JvmName("divEO")
    operator fun <O : SIValue<O>> div(other: O) = divO(other)

    // IMPLEMENTATIONS

    fun timesBA(o: BA) = SIFrac11(tA * (o / bA), bB)
    fun timesBB(o: BB) = SIFrac11(tA * (o / bB), bA)
    fun <O : SIValue<O>> divO(o: O) = SIFrac13(top, SIExp3(bottom.a, bottom.b, o))
}


