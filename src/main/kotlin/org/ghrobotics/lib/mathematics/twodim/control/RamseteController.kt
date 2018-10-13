/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.DeltaTime
import kotlin.math.sin
import kotlin.math.sqrt

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

open class RamseteController(
    trajectory: TimedTrajectory<Pose2dWithCurvature>,
    private val kBeta: Double,
    private val kZeta: Double
) : ITrajectoryFollower {

    private val iterator = trajectory.iterator()

    override var point = iterator.currentState

    override val pose
        get() = point.state.state.pose

    override val isFinished
        get() = iterator.isDone


    // Loops
    private var deltaTimeController = DeltaTime()

    // Returns desired linear and angular velocity of the robot
    override fun getSteering(robot: Pose2d, currentTime: Time): Twist2d {
        val dt = deltaTimeController.updateTime(currentTime)

        return calculateTwist(
            error = pose inFrameOfReferenceOf robot,
            vd = point.state.velocity,
            wd = point.state.velocity * point.state.state.curvature.curvature
        ).also { point = iterator.advance(dt) }

    }

    private fun calculateTwist(error: Pose2d, vd: Double, wd: Double): Twist2d {
        val k1 = gainFunc(vd, wd)
        val angleError = error.rotation.radians
        val delta = Twist2d(
            dxRaw = vd * error.rotation.cos + k1 * error.translation.xRaw,
            dyRaw = 0.0,
            dThetaRaw = wd + kBeta * vd * sinc(angleError) * error.translation.yRaw + k1 * angleError
        )
        return Twist2d(delta.dxRaw, 0.0, delta.dThetaRaw)
    }

    private fun gainFunc(v: Double, w: Double) = 2 * kZeta * sqrt(w * w + kBeta * v * v)

    companion object {
        private fun sinc(theta: Double): Double {
            return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
            else sin(theta) / theta
        }
    }
}