/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.SIUnit

/**
 * Follows a path purely based on linear and angular velocities from the path without any external
 * disturbance correction.
 */
class FeedForwardTracker : TrajectoryTracker() {

    override fun calculateState(
        trajectory: Trajectory,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referenceState = trajectory.currentState

        // Get reference linear and angular velocities
        val vd = referenceState.velocity.value
        val wd = vd * referenceState.state.curvature

        return TrajectoryTrackerVelocityOutput(
            linearVelocity = SIUnit(vd),
            angularVelocity = SIUnit(wd)
        )
    }

}

