/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.math.trajectory.followers

import frc.team5190.lib.extensions.cos
import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Twist2d
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectoryIterator
import frc.team5190.lib.math.trajectory.timing.TimedState
import frc.team5190.lib.math.trajectory.view.TimedView
import kotlin.math.PI
import kotlin.math.sin
import kotlin.math.sqrt

// https://www.dis.uniroma1.it/~labrob/pub/papers/Ramsete01.pdf
// Equation 5.12

class NonLinearController(trajectory: Trajectory<TimedState<Pose2dWithCurvature>>) : TrajectoryFollower {

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
                pathV = this.point.state.velocity,
                pathW = this.point.state.velocity * this.point.state.state.curvature
        ).also { this.point = iterator.advance(dt) }

    }

    companion object {
        private const val kB = 0.4
        private const val kZeta = 0.7

        private const val kMaxSafeV = 12.0
        private const val kMaxSafeW = PI

        fun calculateTwist(error: Pose2d,
                           pathV: Double,
                           pathW: Double) = Twist2d(
                dx = calculateLinearVelocity(error, pathV, pathW).coerceIn(-kMaxSafeV, kMaxSafeV),
                dy = 0.0,
                dtheta = calculateAngularVelocity(error, pathV, pathW).coerceIn(-kMaxSafeW, kMaxSafeW))


        private fun calculateLinearVelocity(error: Pose2d, pathV: Double, pathW: Double): Double {
            return (pathV cos error.rotation.radians) +
                    gainFunc(pathV, pathW) * error.translation.x
        }


        private fun calculateAngularVelocity(error: Pose2d, pathV: Double, pathW: Double): Double {
            return pathW +
                    kB * pathV * sinc(error.rotation.radians) * error.translation.y +
                    gainFunc(pathV, pathW) * error.rotation.radians
        }

        private fun gainFunc(v: Double, w: Double) = 2 * kZeta * sqrt((w * w) + ((kB) * (v * v)))

        private fun sinc(theta: Double): Double {
            return if (theta epsilonEquals 0.0) 1.0 - 1.0 / 6.0 * theta * theta
            else sin(theta) / theta
        }
    }
}