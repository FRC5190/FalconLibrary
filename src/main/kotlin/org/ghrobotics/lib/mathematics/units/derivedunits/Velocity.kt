package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.*

val <T : SIValue<T>> T.velocity: Velocity<T> get() = Velocity(value, this)
val Length.velocity: LinearVelocity get() = Velocity(value, this)
val Rotation2d.velocity: AngularVelocity get() = Velocity(value, this)

typealias LinearVelocity = Velocity<Length>
typealias AngularVelocity = Velocity<Rotation2d>

private val meterToFeet = 1.meter.feet
private val meterToInches = 1.meter.inch
private val secondsPerMinute = 1.minute.second

val LinearVelocity.feetPerSecond get() = value * meterToFeet
val LinearVelocity.feetPerMinute get() = feetPerSecond * secondsPerMinute
val LinearVelocity.inchesPerSecond get() = value * meterToInches

class Velocity<T : SIValue<T>>(
    override val value: Double,
    internal val type: T
) : SIValue<Velocity<T>> {
    override fun createNew(newValue: Double) = Velocity(newValue, type)

    operator fun times(other: Time) = type.createNew(value * other.value)
    operator fun div(other: Time) = Acceleration(value / other.value, type)
    operator fun div(other: Acceleration<T>) = Time(value / other.value)
}