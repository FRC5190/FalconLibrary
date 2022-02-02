/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import com.playingwithfusion.TimeOfFlight
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.milli

/**
 * Returns the distance from the time of flight sensor in a typesafe
 * unit wrapper.
 */
val TimeOfFlight.distance: SIUnit<Meter>
    get() = range.milli.meters
