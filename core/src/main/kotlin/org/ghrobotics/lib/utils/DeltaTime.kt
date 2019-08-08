package org.ghrobotics.lib.utils

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.second

class DeltaTime constructor(startTime: SIUnit<Second> = (-1.0).second) {

    var currentTime = startTime
        private set
    var deltaTime = 0.0.second
        private set

    fun updateTime(newTime: SIUnit<Second>): SIUnit<Second> {
        deltaTime = if (currentTime.value < 0.0) {
            0.0.second
        } else {
            newTime - currentTime
        }
        currentTime = newTime
        return deltaTime
    }

    fun reset() {
        currentTime = (-1.0).second
    }
}