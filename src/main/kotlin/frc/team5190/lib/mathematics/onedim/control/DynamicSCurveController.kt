package frc.team5190.lib.mathematics.onedim.control

class DynamicSCurveController(val x0: Double, val distance: Double,
                              val maxVelocity: Double, val maxAcceleration: Double, maxJerk: Double): DynamicKinematicsController {

    override fun getVelocity(current: Double): Double {
        val displacement = current - x0
        return 0.0
    }

}