/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.mathematics.twodim.trajectory.followers

import frc.team5190.lib.mathematics.epsilonEquals
import frc.team5190.lib.mathematics.twodim.geometry.Pose2d
import frc.team5190.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import frc.team5190.lib.mathematics.twodim.geometry.Twist2d
import frc.team5190.lib.mathematics.twodim.trajectory.Trajectory
import frc.team5190.lib.mathematics.twodim.trajectory.TrajectoryIterator
import frc.team5190.lib.mathematics.twodim.trajectory.timing.TimedState
import frc.team5190.lib.mathematics.twodim.trajectory.view.TimedView
import kotlin.math.sin
import kotlin.math.sqrt

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

class NonLinearController(trajectory: Trajectory<TimedState<Pose2dWithCurvature>>,
                          private val kBeta: Double,
                          private val kZeta: Double) : TrajectoryFollower {

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
                vd = point.state.velocity,
                wd = point.state.velocity * point.state.state.curvature
        ).also { point = iterator.advance(dt) }

    }

    private fun calculateTwist(error: Pose2d, vd: Double, wd: Double) = Twist2d(
            vd * error.rotation.cos + gainFunc(vd, wd) * error.translation.x, 0.0,
            wd + kBeta * sinc(error.rotation.radians) * error.translation.y + gainFunc(vd, wd) * error.rotation.radians
    )

    private fun gainFunc(v: Double, w: Double) = 2 * kZeta * sqrt(w * w + kBeta * v * v)

    private fun sinc(theta: Double): Double {
        return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
        else sin(theta) / theta
    }
}