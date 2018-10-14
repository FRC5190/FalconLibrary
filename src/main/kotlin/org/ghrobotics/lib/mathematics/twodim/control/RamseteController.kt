/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import kotlin.math.sin
import kotlin.math.sqrt

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

open class RamseteController(
        trajectory: TimedTrajectory<Pose2dWithCurvature>,
        drive: DifferentialDrive,
        private val kBeta: Double,
        private val kZeta: Double) : TrajectoryFollower(trajectory, drive) {

    override val chassisVelocity = { robotPose: Pose2d ->

        val error = referencePose inFrameOfReferenceOf robotPose
        val vd = referencePoint.state.velocity
        val wd = vd * referencePoint.state.state.curvature.curvature

        val k1 = 2 * kZeta * sqrt(wd * wd + kBeta * vd * vd)
        val angleError = error.rotation.radians
        DifferentialDrive.ChassisState(
                linear = vd * error.rotation.cos + k1 * error.translation.xRaw,
                angular = wd + kBeta * vd * sinc(angleError) * error.translation.yRaw + k1 * angleError
        )
    }

    companion object {
        private fun sinc(theta: Double): Double {
            return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
            else sin(theta) / theta
        }
    }
}