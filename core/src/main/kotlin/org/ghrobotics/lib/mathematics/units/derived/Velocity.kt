/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

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

@Deprecated("", ReplaceWith("inFeetPerSecond()"))
val SIUnit<LinearVelocity>.feetPerSecond get() = inFeetPerSecond()
@Deprecated("", ReplaceWith("inFeetPerMinute()"))
val SIUnit<LinearVelocity>.feetPerMinute get() = inFeetPerMinute()
@Deprecated("", ReplaceWith("inInchesPerSecond()"))
val SIUnit<LinearVelocity>.inchesPerSecond get() = inInchesPerSecond()

fun SIUnit<LinearVelocity>.inFeetPerSecond() = value.div(kFeetToMeter)
fun SIUnit<LinearVelocity>.inFeetPerMinute() = inFeetPerSecond().times(kMinuteToSecond)
fun SIUnit<LinearVelocity>.inInchesPerSecond() = value.div(kInchToMeter)