package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.units.Time

/**
 * Follows a path purely based on linear and angular velocities from the path without any external
 * disturbance correction.
 */
class FeedForwardTracker : TrajectoryTracker() {

    override fun calculateState(
        iterator: TrajectoryIterator<Time, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referenceState = iterator.currentState.state

        // Get reference linear and angular velocities
        val vd = referenceState._velocity
        val wd = vd * referenceState.state.curvature

        return TrajectoryTrackerVelocityOutput(
            _linearVelocity = vd,
            _angularVelocity = wd
        )
    }

}

