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

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

open class RamseteController(
    drive: DifferentialDrive,
    private val kBeta: Double,
    private val kZeta: Double
) : TrajectoryFollower(drive) {

    override fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState {

        val error = referencePose inFrameOfReferenceOf robotPose
        val vd = referencePoint.state.velocity.value
        val wd = vd * referencePoint.state.state.curvature.curvature.value

        val k1 = 2 * kZeta * sqrt(wd * wd + kBeta * vd * vd)
        val angleError = error.rotation.radian

        return DifferentialDrive.ChassisState(
            linear = vd * error.rotation.cos + k1 * error.translation.x.value,
            angular = wd + kBeta * vd * sinc(angleError) * error.translation.y.value + k1 * angleError
        )
    }

    companion object {
        private fun sinc(theta: Double): Double {
            return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
            else sin(theta) / theta
        }
    }
}