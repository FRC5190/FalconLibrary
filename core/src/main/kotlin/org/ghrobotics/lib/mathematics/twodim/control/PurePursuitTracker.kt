/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Transform2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.*
import kotlin.math.pow

/**
 * Uses an adaptive pure pursuit controller to steer the robot back onto the desired trajectory.
 * From https://www.ri.cmu.edu/pub_files/pub3/coulter_r_craig_1992_1/coulter_r_craig_1992_1.pdf
 *
 * @param kLat Constant of latitude error. Increase this for more aggressive velocity correction if the robot falls behind.
 * @param kLookaheadTime Constant for lookahead time. Larger values mean slower but more stable convergence.
 * @param kMinLookaheadDistance Constant for minimum lookahead distance. Should help with more stability, especially toward the end of
 * the path.
 */
class PurePursuitTracker(
    private val kLat: Double,
    private val kLookaheadTime: SIUnit<Second>,
    private val kMinLookaheadDistance: SIUnit<Meter> = 1.0.meters
) : TrajectoryTracker() {

    /**
     * Calculate desired chassis velocity using pure pursuit.
     */
    override fun calculateState(
        trajectory: Trajectory,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referencePoint = trajectory.currentState

        // Compute the lookahead state.
        val lookaheadState: Pose2d = calculateLookaheadPose2d(trajectory, robotPose)

        // Find the appropriate lookahead point.
        val lookaheadTransform = lookaheadState.relativeTo(robotPose)

        // Calculate latitude error.
        val xError = (referencePoint.state.pose.relativeTo(robotPose)).translation.x

        // Calculate the velocity at the reference point.
        val vd = referencePoint.velocity.value

        // Calculate the distance from the robot to the lookahead.
        val l = lookaheadTransform.translation.norm

        // Calculate the curvature of the arc that connects the robot and the lookahead point.
        val curvature = 2 * lookaheadTransform.translation.y / l.pow(2)

        // Adjust the linear velocity to compensate for the robot lagging behind.
        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return TrajectoryTrackerVelocityOutput(
            linearVelocity = SIUnit(adjustedLinearVelocity),
            // v * curvature = omega
            angularVelocity = SIUnit(adjustedLinearVelocity * curvature)
        )
    }


    private fun calculateLookaheadPose2d(
        trajectory: Trajectory,
        robotPose: Pose2d
    ): Pose2d {
        val lookaheadPoseByTime = trajectory.preview(kLookaheadTime).state.pose

        // The lookahead point is farther from the robot than the minimum lookahead distance.
        // Therefore we can use this point.
        if ((lookaheadPoseByTime.relativeTo(robotPose)).translation.norm >= kMinLookaheadDistance.value) {
            return lookaheadPoseByTime
        }

        var lookaheadPoseByDistance = trajectory.currentState.state.pose

        // We can start previewing from the current lookahead time because we know that we do not
        // meet the distance requirement at this time -- it is useless to start from zero.
        var previewedTime = kLookaheadTime

        // Run the loop until a distance that is greater than the minimum lookahead distance is found or until
        // we run out of "trajectory" to search. If this happens, we will simply extend the end of the trajectory.
        while (trajectory.remainingProgress > previewedTime) {
            previewedTime += 0.02.seconds

            lookaheadPoseByDistance = trajectory.preview(previewedTime).state.pose
            val lookaheadDistance = (lookaheadPoseByDistance.relativeTo(robotPose)).translation.norm

            if (lookaheadDistance > kMinLookaheadDistance.value) {
                return lookaheadPoseByDistance
            }
        }

        // Extend the trajectory.
        val remaining =
            kMinLookaheadDistance.value - (lookaheadPoseByDistance.relativeTo(robotPose)).translation.norm

        return lookaheadPoseByDistance.transformBy(
            Transform2d(
                Translation2d(remaining * (if (trajectory.reversed) -1.0 else 1.0), 0.0),
                Rotation2d()
            )
        )
    }
}