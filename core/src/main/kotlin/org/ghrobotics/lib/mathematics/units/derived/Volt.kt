/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units.derived

import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.Kilogram
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.Mult
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second

typealias Volt = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Ampere, Mult<Second, Mult<Second, Second>>>>

@Deprecated("Replaced with Plural version", ReplaceWith("volts"))
val Double.volt get() = volts

@Deprecated("Replaced with Plural version", ReplaceWith("volts"))
val Number.volt get() = volts

val Double.volts get() = SIUnit<Volt>(this)

val Number.volts get() = toDouble().volts