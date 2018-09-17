package org.ghrobotics.lib.mathematics.onedim.control

class DynamicSCurveController(val x0: Double, val distance: Double,
                              val maxVelocity: Double, val maxAcceleration: Double, maxJerk: Double): DynamicKinematicsController {

    override fun getVelocity(currentPos: Double, nanotime: Long): Double {
        val displacement = currentPos - x0
        return 0.0
    }

}