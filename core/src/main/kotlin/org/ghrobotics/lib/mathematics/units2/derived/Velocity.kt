package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Second
import org.ghrobotics.lib.mathematics.units2.kFeetToMeter
import org.ghrobotics.lib.mathematics.units2.kInchToMeter
import org.ghrobotics.lib.mathematics.units2.kMinuteToSecond

typealias Velocity<K> = Frac<K, Second>

typealias LinearVelocity = Velocity<Meter>
typealias AngularVelocity = Velocity<Radian>

val <K : SIKey> SIUnit<K>.velocity get() = SIUnit<Velocity<K>>(value)

val SIUnit<LinearVelocity>.feetPerSecond get() = value.div(kFeetToMeter)
val SIUnit<LinearVelocity>.feetPerMinute get() = feetPerSecond.div(kMinuteToSecond)
val SIUnit<LinearVelocity>.inchesPerSecond get() = value.div(kInchToMeter)