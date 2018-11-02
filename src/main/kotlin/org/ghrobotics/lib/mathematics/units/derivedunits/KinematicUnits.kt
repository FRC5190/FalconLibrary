package org.ghrobotics.lib.mathematics.units.derivedunits

/* ktlint-disable no-wildcard-imports */
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac13

val Length.velocity: Velocity get() = this / 1.second
val Length.acceleration: Acceleration get() = this.velocity / 1.second

typealias Velocity = SIFrac11<Length, Time>
typealias Speed = Velocity
typealias Acceleration = SIFrac12<Length, Time, Time>
typealias Jerk = SIFrac13<Length, Time, Time, Time>
typealias Jolt = Jerk

val meterToFeet = 1.meter.feet
val meterToInches = 1.meter.inch
val secondsPerMinute = 1.minute.second

val Velocity.feetPerSecond get() = value * meterToFeet
val Velocity.feetPerMinute get() = feetPerSecond * secondsPerMinute
val Velocity.inchesPerSecond get() = value * meterToInches