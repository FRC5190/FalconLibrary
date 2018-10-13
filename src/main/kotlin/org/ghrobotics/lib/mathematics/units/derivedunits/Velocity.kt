package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.*

infix fun Length.per(other: Time): Velocity = div(other)
operator fun Length.div(other: Time): Velocity = VelocityImpl(this, other)

val Length.velocity: Velocity
    get() = VelocityImpl(
        this,
        1.second
    )

interface Velocity : SIFraction<Length, LengthUnits, Time, TimeUnits, Velocity> {
    val length: Length
    val time: Time

    operator fun div(other: Acceleration): Time

    val feetPerSecond
        get() = adjust(LengthUnits.Feet, TimeUnits.Second)

    val inchesPerSecond
        get() = adjust(LengthUnits.Inch, TimeUnits.Second)

    companion object {
        val NEGATIVE_INFINITY: Velocity = VelocityImpl(Double.NEGATIVE_INFINITY.meter, 1.second)
        val POSITIVE_INFINITY: Velocity = VelocityImpl(Double.POSITIVE_INFINITY.meter, 1.second)

        val ZERO: Velocity = VelocityImpl(0.meter, 1.second)
    }
}

class VelocityImpl(
    override val length: Length,
    override val time: Time
) : Velocity, AbstractSIFraction<Length, LengthUnits, Time, TimeUnits, Velocity>(
    length,
    time
) {
    override fun create(newTop: Length, newBottom: Time) = VelocityImpl(newTop, newBottom)

    override fun div(other: Acceleration): Time {
        val vLength = length.asMetric
        val vTime = time.asMetric

        val aTime1 = other.top.time.asMetric
        val aTime2 = other.bottom
        val aLength = other.top.length.asMetric

        return (aTime2 * vLength.asDouble * aTime1.asDouble) / (vTime.asDouble * aLength.asDouble)
    }
}