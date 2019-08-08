package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Second

typealias Acceleration<K> = Frac<Frac<K, Second>, Second>

typealias LinearAcceleration = Acceleration<Meter>
typealias AngularAcceleration = Acceleration<Radian>

val <K : SIKey> SIUnit<K>.acceleration get() = SIUnit<Acceleration<K>>(value)
