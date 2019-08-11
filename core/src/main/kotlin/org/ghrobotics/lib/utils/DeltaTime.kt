/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.seconds

class DeltaTime constructor(startTime: SIUnit<Second> = (-1.0).seconds) {

    var currentTime = startTime
        private set
    var deltaTime = 0.0.seconds
        private set

    fun updateTime(newTime: SIUnit<Second>): SIUnit<Second> {
        deltaTime = if (currentTime.value < 0.0) {
            0.0.seconds
        } else {
            newTime - currentTime
        }
        currentTime = newTime
        return deltaTime
    }

    fun reset() {
        currentTime = (-1.0).seconds
    }
}