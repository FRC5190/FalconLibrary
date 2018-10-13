package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.AbstractSIValue
import org.ghrobotics.lib.mathematics.units.SIValue
import kotlin.math.absoluteValue

val Number.volt: Volt
    get() = VoltImpl(toDouble())

interface Volt : SIValue<Volt>

class VoltImpl(
    value: Double
) : AbstractSIValue<Volt>(), Volt {
    override val asDouble = value
    override val asMetric = this

    override val absoluteValue by lazy { VoltImpl(asDouble.absoluteValue) }

    override fun unaryMinus() = VoltImpl(-asDouble)

    override fun plus(other: Volt) = VoltImpl(asDouble + other.asDouble)
    override fun minus(other: Volt) = VoltImpl(asDouble - other.asDouble)

    override fun div(other: Volt) = asDouble / other.asDouble

    override fun times(other: Number) = VoltImpl(asDouble * other.toDouble())
    override fun div(other: Number) = VoltImpl(asDouble / other.toDouble())

    override fun compareTo(other: Volt) = asDouble.compareTo(other.asDouble)
}