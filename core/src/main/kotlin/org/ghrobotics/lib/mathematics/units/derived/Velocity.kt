package org.ghrobotics.lib.mathematics.units.derived

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.kFeetToMeter
import org.ghrobotics.lib.mathematics.units.kInchToMeter
import org.ghrobotics.lib.mathematics.units.kMinuteToSecond

typealias Velocity<K> = Frac<K, Second>

typealias LinearVelocity = Velocity<Meter>
typealias AngularVelocity = Velocity<Radian>

val <K : SIKey> SIUnit<K>.velocity get() = SIUnit<Velocity<K>>(value)

val SIUnit<LinearVelocity>.feetPerSecond get() = value.div(kFeetToMeter)
val SIUnit<LinearVelocity>.feetPerMinute get() = feetPerSecond.times(kMinuteToSecond)
val SIUnit<LinearVelocity>.inchesPerSecond get() = value.div(kInchToMeter)