package frc.team5190.lib.mathematics.onedim.control

interface DynamicKinematicsController {
    fun getVelocity(current: Double): Double
}