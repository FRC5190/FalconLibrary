package org.ghrobotics.lib.mathematics.onedim.control

import koma.pow

class DynamicSCurveController(val x0: Double, val distance: Double,
                              val maxVelocity: Double, val maxAcceleration: Double, val jerk: Double) : DynamicKinematicsController {


    private val xConcave = maxAcceleration.pow(3) / (6 * jerk.pow(2))
    private val vConcave = maxAcceleration.pow(2) / (2 * jerk)
    private val xLinear  = ((maxVelocity - vConcave).pow(2) - vConcave.pow(2)) / (2 * maxAcceleration)
    private val xConvex  = ((maxVelocity - (maxAcceleration.pow(2) / (2 * jerk))) * (maxAcceleration / jerk)) +
            (maxAcceleration.pow(3) / (2 * jerk.pow(2))) - (maxAcceleration.pow(3) / (6 * jerk.pow(2)))



    private val xAccel = xConcave + xLinear + xConvex
    private val xCruise = distance - 2 * xAccel

    private var velocityCommand = 0.0
    private var acceleration = 0.0

    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0

    /*
    init {
        println("X Concave: $xConcave")
        println("V Concave: ${maxAcceleration.pow(2) / (2 * jerk)}")
        println("X Linear: $xLinear")
        println("V Linear: ${maxVelocity - (maxAcceleration.pow(2) / (2 * jerk))}")
        println("X Convex: $xConvex")
    }
    */

    override fun getVelocity(currentPos: Double, nanotime: Long): Double {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9

        val displacement = currentPos - x0
        acceleration = when {
            displacement < xConcave                             -> (acceleration + jerk * dt).coerceAtMost(maxAcceleration)
            displacement < xConcave + xLinear                   -> maxAcceleration
            displacement < xConcave + xLinear + xConvex         -> (acceleration - jerk * dt).coerceAtLeast(0.0)
            displacement < xAccel + xCruise                     -> 0.0
            displacement < xAccel + xCruise + xConvex           -> (acceleration - jerk * dt).coerceAtLeast(-maxAcceleration)
            displacement < xAccel + xCruise + xConvex + xLinear -> -maxAcceleration
            displacement < xAccel + xCruise + xAccel            -> (acceleration + jerk * dt).coerceAtMost(0.0)
            else                                                -> 0.0
        }
        velocityCommand = (velocityCommand + acceleration * dt).coerceIn(0.0, maxVelocity)
        return velocityCommand
    }
}