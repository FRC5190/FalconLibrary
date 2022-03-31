/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.utils

/**
 * Team 254's 2019 Util code
 */
object Util {
    const val kEpsilon = 1e-12

    /**
     * Limits the given input to the given magnitude.
     */
    fun limit(v: Double, maxMagnitude: Double): Double {
        return limit(v, -maxMagnitude, maxMagnitude)
    }

    fun limit(v: Double, min: Double, max: Double): Double {
        return max.coerceAtMost(min.coerceAtLeast(v))
    }

    fun inRange(v: Double, maxMagnitude: Double): Boolean {
        return inRange(v, -maxMagnitude, maxMagnitude)
    }

    /**
     * Checks if the given input is within the range (min, max), both exclusive.
     */
    fun inRange(v: Double, min: Double, max: Double): Boolean {
        return v > min && v < max
    }

    fun interpolate(a: Double, b: Double, x: Double): Double {
        var x = x
        x = limit(x, 0.0, 1.0)
        return a + (b - a) * x
    }

    fun joinStrings(delim: String?, strings: List<*>): String {
        val sb = StringBuilder()
        for (i in strings.indices) {
            sb.append(strings[i].toString())
            if (i < strings.size - 1) {
                sb.append(delim)
            }
        }
        return sb.toString()
    }

    @JvmOverloads
    fun epsilonEquals(a: Double, b: Double, epsilon: Double = kEpsilon): Boolean {
        return a - epsilon <= b && a + epsilon >= b
    }

    fun epsilonEquals(a: Int, b: Int, epsilon: Int): Boolean {
        return a - epsilon <= b && a + epsilon >= b
    }

    fun allCloseTo(list: List<Double>, value: Double, epsilon: Double): Boolean {
        var result = true
        for (value_in in list) {
            result = result and epsilonEquals(value_in, value, epsilon)
        }
        return result
    }

    fun bound0To2PIRadians(radians: Double): Double {
        return Math.toRadians(bound0To360Degrees(Math.toDegrees(radians)))
    }

    fun bound0To360Degrees(degrees: Double): Double {
        var degrees = degrees
        degrees %= 360.0
        if (degrees < 0) {
            degrees += 360.0
        }
        return degrees
    }
}
