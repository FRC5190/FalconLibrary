package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac13
import org.ghrobotics.lib.mathematics.units.fractions.adjust

val Length.velocity: Velocity
    get() = this per 1.second

val Length.acceleration: Acceleration
    get() = this.velocity per 1.second

typealias Velocity = SIFrac11<Length, Time>
typealias Speed = Velocity
typealias Acceleration = SIFrac12<Length, Time, Time>
typealias Jerk = SIFrac13<Length, Time, Time, Time>
typealias Jolt = Jerk

val Velocity.feetPerSecond
    get() = adjust(
            SIPrefix.BASE, LengthUnits.Feet,
            SIPrefix.BASE, TimeUnits.Second
    )

val Velocity.inchesPerSecond
    get() = adjust(
            SIPrefix.BASE, LengthUnits.Inch,
            SIPrefix.BASE, TimeUnits.Second
    )


/*
interface Velocity : SIFrac<Length, LengthUnits, Time, TimeUnits, Velocity> {
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
) : Velocity, AbstractSIFrac<Length, LengthUnits, Time, TimeUnits, Velocity>(
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
}*/