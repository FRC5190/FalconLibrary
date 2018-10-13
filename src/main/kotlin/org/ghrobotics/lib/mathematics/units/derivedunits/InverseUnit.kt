package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.AbstractSIValue
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue
import kotlin.math.absoluteValue

operator fun <T : SIUnit<T, TE>, TE : Enum<TE>> Number.div(other: T): InverseUnit<T, TE> =
    InverseUnitImpl(this.toDouble(), other)

interface InverseUnit<T : SIUnit<T, TE>, TE : Enum<TE>> : SIValue<InverseUnit<T, TE>> {
    val top: Double
    val bottom: T
}

class InverseUnitImpl<T : SIUnit<T, TE>, TE : Enum<TE>>(
    override val top: Double,
    override val bottom: T
) : AbstractSIValue<InverseUnit<T, TE>>(), InverseUnit<T, TE> {

    override val asDouble = top / bottom.asDouble

    override val asMetric by lazy { InverseUnitImpl(top, bottom.asMetric) }
    override val absoluteValue by lazy { InverseUnitImpl(top.absoluteValue, bottom.absoluteValue) }

    override fun unaryMinus() = InverseUnitImpl(-top, bottom)

    override fun div(other: InverseUnit<T, TE>): Double {
        TODO("not implemented")
    }

    override fun plus(other: InverseUnit<T, TE>): InverseUnit<T, TE> {
        TODO("not implemented")
    }

    override fun minus(other: InverseUnit<T, TE>): InverseUnit<T, TE> {
        TODO("not implemented")
    }

    override fun times(other: Number) = InverseUnitImpl(top * other.toDouble(), bottom)
    override fun div(other: Number) = InverseUnitImpl(top, bottom * other)

    override fun compareTo(other: InverseUnit<T, TE>): Int {
        TODO("not implemented")
    }


}