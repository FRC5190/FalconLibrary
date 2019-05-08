package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.SIValue

typealias Curvature = InverseUnit<Length>

@Suppress("FunctionName")
fun Curvature(value: Double) = Curvature(value, Length.kZero)

operator fun <T : SIUnit<T>> Number.div(other: T): InverseUnit<T> =
    InverseUnit(this.toDouble(), other)

class InverseUnit<T : SIUnit<T>>(
    override val value: Double,
    val type: T
) : SIValue<InverseUnit<T>> {
    override fun createNew(newValue: Double) = InverseUnit(newValue, type)
}