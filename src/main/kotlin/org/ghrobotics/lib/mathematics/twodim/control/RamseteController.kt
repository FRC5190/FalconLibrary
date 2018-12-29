/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Uses a time-varying non linear reference controller to steer the robot back onto the trajectory.
 * From https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf eq 5.12
 *
 * @param drive Instance of the differential drive that represents the dynamics of the drivetrain.
 * @param kBeta Constant for correction. Increase for more aggressive convergence.
 * @param kZeta Constant for dampening. Increase for more dampening.
 */
open class RamseteController(
    drive: DifferentialDrive,
    private val kBeta: Double,
    private val kZeta: Double
) : TrajectoryFollower(drive) {

    /**
     * Calculate desired chassis velocity using Ramsete.
     */
    override fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState {

        // Calculate goal in robot's coordinates
        val error = referencePose inFrameOfReferenceOf robotPose

        // Get reference linear and angular velocities
        val vd = referencePoint.state._velocity
        val wd = vd * referencePoint.state.state.curvature._curvature

        // Compute gain
        val k1 = 2 * kZeta * sqrt(wd * wd + kBeta * vd * vd)

        // Get angular error in bounded radians
        val angleError = error.rotation.radian

        return DifferentialDrive.ChassisState(
            linear = vd * error.rotation.cos + k1 * error.translation._x,
            angular = wd + kBeta * vd * sinc(angleError) * error.translation._y + k1 * angleError
        )
    }

    companion object {
        private fun sinc(theta: Double) =
            if (theta epsilonEquals 0.0) {
                1.0 - 1.0 / 6.0 * theta * theta
            } else sin(theta) / theta
    }
}