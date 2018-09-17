package org.ghrobotics.lib.mathematics.onedim.control

import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "unused")
class DynamicTCurveController(val x0: Double, val distance: Double,
                              val maxVelocity: Double, val maxAcceleration: Double) : DynamicKinematicsController {

    /**
     * Calculates the distance required to accelerate to cruise velocity
     * v^2 = v0^2 + 2ax
     * v0 in this case is zero.
     * v^2 = 2ax
     * x = v^2 / 2a
     */
    private val xAccel = (maxVelocity.pow(2) / (2 * maxAcceleration)).let{
        if (it > distance / 2.0) {
            distance / 2.0
        } else it
    }

    /**
     * Calculates the distance required for cruising
     * xAccel + xAccel + xCruise = distance
     */
    private val xCruise = distance - (2 * xAccel)

    private var velocityCommand = 0.0

    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0

    override fun getVelocity(currentPos: Double, nanotime: Long): Double {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9

        val displacement = currentPos - x0
        when {
            // Acceleration Phase
            displacement < xAccel -> {
                velocityCommand = (velocityCommand + maxAcceleration * dt).coerceAtMost(maxVelocity)
            }
            // Deceleration Phase
            displacement >= xAccel +  xCruise -> {
                velocityCommand = (velocityCommand - maxAcceleration * dt).coerceAtLeast(0.0)
            }
        }
        return velocityCommand
    }
}