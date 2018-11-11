package org.ghrobotics.lib.utils

import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second

class DeltaTime internal constructor(startTime: Double) {
    constructor(startTime: Time = (-1).second) : this(startTime.value)

    internal var _currentTime = startTime
        private set
    internal var _deltaTime = 0.0
        private set

    val deltaTime get() = _deltaTime.second
    val currentTime get() = _currentTime.second

    fun updateTime(newTime: Time) = updateTime(newTime.value).second

    internal fun updateTime(newTime: Double): Double {
        _deltaTime = if (_currentTime < 0.0) {
            0.0
        } else {
            newTime - _currentTime
        }
        _currentTime = newTime
        return _deltaTime
    }

    fun reset() {
        _currentTime = -1.0
    }
}