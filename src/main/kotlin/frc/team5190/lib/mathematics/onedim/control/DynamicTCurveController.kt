package frc.team5190.lib.mathematics.onedim.control

import kotlin.math.pow

class DynamicTCurveController(val x0: Double, val distance: Double, val dt: Double,
                              val maxVelocity: Double, val maxAcceleration: Double) : DynamicKinematicsController {

    private val xAccel = maxVelocity.pow(2) / (2 * maxAcceleration)
    private val xCruise = distance - (2 * xAccel)

    private var commandedVelocity = 0.0

    override fun getVelocity(current: Double): Double {
        val displacement = current - x0
        when {
            displacement <= xAccel -> {
                commandedVelocity = Math.max(commandedVelocity + maxAcceleration * dt, maxVelocity)
            }
            displacement >= xCruise -> {
                commandedVelocity = Math.min(0.0, commandedVelocity - maxAcceleration * dt)
            }
        }
        return commandedVelocity
    }

}