package org.ghrobotics.lib.utils

import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second

class DeltaTime(startTime: Time = (-1).second) {
    var deltaTime = 0.second
        private set
    var currentTime = startTime
        private set

    fun updateTime(newTime: Time): Time {
        deltaTime = if (currentTime.asDouble < 0.0) {
            0.second
        } else {
            newTime - currentTime
        }
        currentTime = newTime
        return deltaTime
    }
}