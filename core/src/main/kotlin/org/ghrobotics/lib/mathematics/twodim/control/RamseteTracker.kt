package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.units.Time
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
        iterator: TrajectoryIterator<Time, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput {
        val referenceState = iterator.currentState.state

        // Calculate goal in robot's coordinates
        val error = referenceState.state.pose inFrameOfReferenceOf robotPose

        // Get reference linear and angular velocities
        val vd = referenceState._velocity
        val wd = vd * referenceState.state.curvature

        // Compute gain
        val k1 = 2 * kZeta * sqrt(wd * wd + kBeta * vd * vd)

        // Get angular error in bounded radians
        val angleError = error.rotation.radian

        return TrajectoryTrackerVelocityOutput(
            _linearVelocity = vd * error.rotation.cos + k1 * error.translation.x,
            _angularVelocity = wd + kBeta * vd * sinc(angleError) * error.translation.y + k1 * angleError
        )
    }

    companion object {
        private fun sinc(theta: Double) =
            if (theta epsilonEquals 0.0) {
                1.0 - 1.0 / 6.0 * theta * theta
            } else sin(theta) / theta
    }

}

