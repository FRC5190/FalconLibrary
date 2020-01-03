/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import com.cuforge.libcu.Lasershark
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.inches

/**
 * Returns the distance from the rangefinder sensor in a typesafe
 * unit wrapper.
 */
val Lasershark.distance: SIUnit<Meter>
    get() = distanceInches.inches