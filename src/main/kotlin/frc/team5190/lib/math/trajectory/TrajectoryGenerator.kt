/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.trajectory

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Rotation2d
import frc.team5190.lib.math.trajectory.timing.TimedState
import frc.team5190.lib.math.trajectory.timing.TimingConstraint
import frc.team5190.lib.math.trajectory.timing.TimingUtil
import frc.team5190.lib.math.trajectory.view.DistanceView

object TrajectoryGenerator {

    private const val kMaxDx = 2.0 / 12.0
    private const val kMaxDy = 0.25 / 12.0
    private val kMaxDTheta = Math.toDegrees(5.0)


    // Generate trajectory with custom start and end velocity.
    fun generateTrajectory(
            reversed: Boolean,
            waypoints: ArrayList<Pose2d>,
            constraints: ArrayList<TimingConstraint<Pose2dWithCurvature>>,
            startVel: Double,
            endVel: Double,
            maxVelocity: Double,
            maxAcceleration: Double
    ): Trajectory<TimedState<Pose2dWithCurvature>>? {

        val flippedPose2d = Pose2d.fromRotation(Rotation2d.fromDegrees(180.0))

        // Make theta normal for trajectory generation if path is trajectoryReversed.
        val newWaypoints = waypoints.map { if (reversed) it.transformBy(flippedPose2d) else it }

        var trajectory = TrajectoryUtil.trajectoryFromSplineWaypoints(newWaypoints, kMaxDx, kMaxDy, kMaxDTheta)

        // After trajectory generation, flip theta back so it's relative to the field.
        // Also fix curvature and its derivative
        if (reversed) {
            val points = ArrayList<Pose2dWithCurvature>(trajectory.length)
            for (i in 0 until trajectory.length) {
                points.add(Pose2dWithCurvature(
                        pose = trajectory.getState(i).pose.transformBy(flippedPose2d),
                        curvature = -trajectory.getState(i).curvature,
                        dcurvature_ds = -trajectory.getState(i).dkds
                ))
            }
            trajectory = Trajectory(points)
        }

        // Parameterize by time and return.
        return TimingUtil.timeParameterizeTrajectory(reversed, DistanceView(trajectory), kMaxDx, constraints,
                startVel, endVel, maxVelocity, maxAcceleration)
    }
}