package org.ghrobotics.lib.mathematics.onedim.control

import koma.max

class DynamicSCurveController(val x0: Double, val distance: Double,
                              val maxVelocity: Double, val maxAcceleration: Double, val maxJerk: Double) : DynamicKinematicsController {

    private val xConcave = 0.0
    private val xLinear = 0.0

    private val xAccel = 2 * xConcave + xLinear
    private val xCruise = distance - 2 * (xConcave + xConcave + xLinear)

    private var velocityCommand = 0.0
    private var acceleration = 0.0

    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0

    override fun getVelocity(currentPos: Double, nanotime: Long): Double {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9

        val displacement = currentPos - x0
        acceleration = when {
            displacement < xConcave                                 -> (acceleration + maxJerk * dt).coerceAtMost(maxAcceleration)
            displacement < xConcave + xLinear                       -> maxAcceleration
            displacement < xAccel                                   -> (acceleration - maxJerk * dt).coerceAtLeast(0.0)
            displacement < xAccel + xCruise                         -> 0.0
            displacement < xAccel + xCruise + xConcave              -> (acceleration - maxJerk * dt).coerceAtLeast(-maxAcceleration)
            displacement < xAccel + xCruise + xConcave + xLinear    -> -maxAcceleration
            displacement < xAccel + xCruise + xAccel                -> (acceleration + maxJerk * dt).coerceAtMost(0.0)
            else                                                    -> 0.0
        }
        velocityCommand = (velocityCommand + acceleration * dt).coerceIn(0.0, maxVelocity)
        return velocityCommand
    }

}