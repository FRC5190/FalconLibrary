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

typealias Ohm = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Second, Mult<Second, Mult<Second, Mult<Ampere, Ampere>>>>>

@Deprecated("Replaced with Plural version", ReplaceWith("ohms"))
val Double.ohm get() = ohms

@Deprecated("Replaced with Plural version", ReplaceWith("ohms"))
val Number.ohm get() = ohms

val Double.ohms get() = SIUnit<Ohm>(this)

val Number.ohms get() = toDouble().ohms