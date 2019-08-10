/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.SIUnit
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Uses a time-varying non linear reference controller to steer the robot back onto the trajectory.
 * From https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf eq 5.12
 *
 * @param kBeta Constant for correction. Increase for more aggressive convergence.
 * @param kZeta Constant for dampening. Increase for more dampening.
 */
class RamseteTracker(
    private val kBeta: Double,
    private val kZeta: Double
) : TrajectoryTracker() {

    /**
     * Calculate desired chassis velocity using Ramsete.
     */
    override fun calculateState(
        trajectory: Trajectory,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referenceState = trajectory.currentState

        // Calculate goal in robot's coordinates
        val error = referenceState.state.pose.relativeTo(robotPose)

        // Get reference linear and angular velocities
        val vd = referenceState.velocity.value
        val wd = vd * referenceState.state.curvature

        // Compute gain
        val k1 = 2 * kZeta * sqrt(wd * wd + kBeta * vd * vd)

        // Get angular error in bounded radians
        val angleError = error.rotation.radians

        return TrajectoryTrackerVelocityOutput(
            linearVelocity = SIUnit(vd * error.rotation.cos + k1 * error.translation.x),
            angularVelocity = SIUnit(wd + kBeta * vd * sinc(angleError) * error.translation.y + k1 * angleError)
        )
    }

    companion object {
        private fun sinc(theta: Double) =
            if (theta epsilonEquals 0.0) {
                1.0 - 1.0 / 6.0 * theta * theta
            } else sin(theta) / theta
    }

}

