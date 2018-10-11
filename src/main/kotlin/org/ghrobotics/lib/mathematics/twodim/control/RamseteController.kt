/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TimedState
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.view.TimedView
import kotlin.math.sin
import kotlin.math.sqrt

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

open class RamseteController(trajectory: Trajectory<TimedState<Pose2dWithCurvature>>,
                             private val kBeta: Double,
                             private val kZeta: Double) : ITrajectoryFollower {

    private val iterator = TrajectoryIterator(TimedView(trajectory))

    override var point = iterator.preview(0.0)

    override val pose
        get() = point.state.state.pose

    override val isFinished
        get() = iterator.isDone


    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0

    // Returns desired linear and angular velocity of the robot
    override fun getSteering(robot: Pose2d, nanotime: Long): Twist2d {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9

        return calculateTwist(
                error = pose inFrameOfReferenceOf robot,
                vd = ftm(point.state.velocity),
                wd = point.state.velocity * point.state.state.curvature
        ).also { point = iterator.advance(dt) }

    }

    private fun calculateTwist(error: Pose2d, vd: Double, wd: Double): Twist2d {
        val k1 = gainFunc(vd, wd)
        val angleError = error.rotation.radians
        val delta = Twist2d(
                dx = vd * error.rotation.cos + k1 * ftm(error.translation.x),
                dy = 0.0,
                dtheta = wd + kBeta * vd * sinc(angleError) * ftm(error.translation.y) + k1 * angleError)
        return Twist2d(mtf(delta.dx), 0.0, delta.dtheta)
    }

    private fun gainFunc(v: Double, w: Double) = 2 * kZeta * sqrt(w * w + kBeta * v * v)

    companion object {
        private fun sinc(theta: Double): Double {
            return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
            else sin(theta) / theta
        }
        private fun ftm(f: Double) = f * 12.0 * 0.0254
        private fun mtf(m: Double) = m / 0.0254 / 12.0
    }
}