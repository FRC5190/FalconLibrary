/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.operations.times

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class FunctionalLinearSpline(
    val p1: Translation2d,
    val p2: Translation2d
) {
    val m: Double get() = ((p2.y - p1.y) / (p2.x - p1.x)).value
    val b: SIUnit<Meter> get() = p1.y - (m * p1.x)

    val zero: SIUnit<Meter> get() = -b / m
}