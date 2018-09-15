package org.ghrobotics.lib.mathematics.onedim.control

import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "unused")
class DynamicTCurveController(val x0: Double, val distance: Double, val dt: Double,
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

    override fun getVelocity(current: Double): Double {
        val displacement = current - x0
        when {
            // Acceleration Phase
            displacement <= xAccel -> {
                velocityCommand = Math.max(velocityCommand + maxAcceleration * dt, maxVelocity)
            }
            // Deceleration Phase
            displacement >= xAccel +  xCruise -> {
                velocityCommand = Math.min(0.0, velocityCommand - maxAcceleration * dt)
            }
        }
        return velocityCommand
    }
}