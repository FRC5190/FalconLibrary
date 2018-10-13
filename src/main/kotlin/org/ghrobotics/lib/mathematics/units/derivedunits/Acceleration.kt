package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.*

infix fun Velocity.per(other: Time): Acceleration = div(other)
operator fun Velocity.div(other: Time): Acceleration = AccelerationImpl(this, other)

val Length.acceleration: Acceleration
    get() = AccelerationImpl(
        this.velocity,
        1.second
    )

interface Acceleration : SIChainedFraction<Velocity, Time, TimeUnits, Acceleration> {
    val length: Length
    val timeSquared: Time

    operator fun div(other: InverseUnit<Length, LengthUnits>): Velocity

    companion object {
        val NEGATIVE_INFINITY: Acceleration = AccelerationImpl(Velocity.NEGATIVE_INFINITY, 1.second)
        val POSITIVE_INFINITY: Acceleration = AccelerationImpl(Velocity.POSITIVE_INFINITY, 1.second)

        val ZERO: Acceleration = AccelerationImpl(Velocity.ZERO, 1.second)
    }
}

class AccelerationImpl(
    velocity: Velocity,
    time: Time
) : Acceleration, AbstractSIChainedFraction<Velocity, Time, TimeUnits, Acceleration>(
    velocity,
    time
) {
    override val length: Length = velocity.length
    override val timeSquared: Time by lazy { velocity.time.second * time.second.asDouble }

    override fun create(newTop: Velocity, newBottom: Time) = AccelerationImpl(newTop, newBottom)

    override fun div(other: InverseUnit<Length, LengthUnits>): Velocity {
        val aLength = length.asMetric
        val oLength = other.bottom.asMetric

        return Math.sqrt(aLength.asDouble * oLength.asDouble).meter /
                Math.sqrt(top.time.second.asDouble * bottom.second.asDouble).second
    }
}

