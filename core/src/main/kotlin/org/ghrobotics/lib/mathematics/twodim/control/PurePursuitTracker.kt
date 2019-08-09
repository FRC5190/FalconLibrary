/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
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
    private val kMinLookaheadDistance: SIUnit<Meter> = 1.0.meter
) : TrajectoryTracker() {

    /**
     * Calculate desired chassis velocity using pure pursuit.
     */
    override fun calculateState(
        iterator: TrajectoryIterator<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referencePoint = iterator.currentState

        // Compute the lookahead state.
        val lookaheadState: Pose2d = calculateLookaheadPose2d(iterator, robotPose)

        // Find the appropriate lookahead point.
        val lookaheadTransform = lookaheadState inFrameOfReferenceOf robotPose

        // Calculate latitude error.
        val xError = (referencePoint.state.state.pose inFrameOfReferenceOf robotPose).translation.x.value

        // Calculate the velocity at the reference point.
        val vd = referencePoint.state.velocity.value

        // Calculate the distance from the robot to the lookahead.
        val l = lookaheadTransform.translation.norm.value

        // Calculate the curvature of the arc that connects the robot and the lookahead point.
        val curvature = 2 * lookaheadTransform.translation.y.value / l.pow(2)

        // Adjust the linear velocity to compensate for the robot lagging behind.
        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return TrajectoryTrackerVelocityOutput(
            linearVelocity = SIUnit(adjustedLinearVelocity),
            // v * curvature = omega
            angularVelocity = SIUnit(adjustedLinearVelocity * curvature)
        )
    }


    private fun calculateLookaheadPose2d(
        iterator: TrajectoryIterator<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): Pose2d {
        val lookaheadPoseByTime = iterator.preview(kLookaheadTime).state.state.pose

        // The lookahead point is farther from the robot than the minimum lookahead distance.
        // Therefore we can use this point.
        if ((lookaheadPoseByTime inFrameOfReferenceOf robotPose).translation.norm >= kMinLookaheadDistance) {
            return lookaheadPoseByTime
        }

        val lastInterpolant = iterator.trajectory.lastInterpolant

        var lookaheadPoseByDistance = iterator.currentState.state.state.pose

        // We can start previewing from the current lookahead time because we know that we do not
        // meet the distance requirement at this time -- it is useless to start from zero.
        var previewedTime = kLookaheadTime

        // Run the loop until a distance that is greater than the minimum lookahead distance is found or until
        // we run out of "trajectory" to search. If this happens, we will simply extend the end of the trajectory.
        while ((lastInterpolant - iterator.progress) > previewedTime) {
            previewedTime += 0.02.second

            lookaheadPoseByDistance = iterator.preview(previewedTime).state.state.pose
            val lookaheadDistance = (lookaheadPoseByDistance inFrameOfReferenceOf robotPose).translation.norm

            if (lookaheadDistance > kMinLookaheadDistance) {
                return lookaheadPoseByDistance
            }
        }

        // Extend the trajectory.
        val remaining =
            kMinLookaheadDistance - (lookaheadPoseByDistance inFrameOfReferenceOf robotPose).translation.norm

        return lookaheadPoseByDistance.transformBy(
            Pose2d(
                Translation2d(remaining * (if (iterator.trajectory.reversed) -1.0 else 1.0), 0.0.meter)
            )
        )
    }

}