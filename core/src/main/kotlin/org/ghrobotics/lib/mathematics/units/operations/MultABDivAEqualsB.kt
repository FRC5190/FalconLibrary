/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.Mult
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit

// m^3 / m^2 = m

// SIUnit<Mult<Mult<Meter, Meter>, Meter> / SIUnit<Mult<Meter, Meter>> = SIUnit<Meter>
// SIUnit<Mult<A, B>> / SIUnit<A> = SIUnit<B>

operator fun <A : SIKey, B : SIKey> SIUnit<Mult<A, B>>.div(other: SIUnit<A>) = SIUnit<B>(value.div(other.value))
