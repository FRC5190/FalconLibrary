package frc.team5190.lib.mathematics.twodim.trajectory

import frc.team5190.lib.mathematics.twodim.geometry.Pose2d

data class TrajectoryContainer(val name: String,
                               val reversed: Boolean,
                               val waypoints: ArrayList<Pose2d>,
                               val startVelocity: Double,
                               val endVelocity: Double,
                               val maxVelocity: Double,
                               val maxAcceleration: Double,
                               val maxCentripetalAcceleration: Double) {
    override fun toString() = name
}