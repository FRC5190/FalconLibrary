/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.spline

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Rotation2d
import frc.team5190.lib.math.geometry.Translation2d

abstract class Spline {
    abstract fun getPoint(t: Double): Translation2d
    abstract fun getHeading(t: Double): Rotation2d
    abstract fun getCurvature(t: Double): Double
    abstract fun getDCurvature(t: Double): Double
    abstract fun getVelocity(t: Double): Double

    private fun getPose2d(t: Double): Pose2d {
        return Pose2d(getPoint(t), getHeading(t))
    }

    fun getPose2dWithCurvature(t: Double): Pose2dWithCurvature {
        return Pose2dWithCurvature(getPose2d(t), getCurvature(t), getDCurvature(t) / getVelocity(t))
    }
}
