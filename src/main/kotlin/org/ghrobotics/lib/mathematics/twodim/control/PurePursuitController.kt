package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TimedState
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.view.TimedView
import kotlin.math.pow

class PurePursuitController(trajectory: Trajectory<TimedState<Pose2dWithCurvature>>,
                            private val kLat: Double,
                            private val kLookaheadTime: Double) : ITrajectoryFollower {

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
                lookaheadTransform = iterator.preview(kLookaheadTime).state.state.pose inFrameOfReferenceOf robot,
                xError = (pose inFrameOfReferenceOf robot).translation.x,
                vd = point.state.velocity
        ).also { point = iterator.advance(dt) }
    }

    private fun calculateTwist(lookaheadTransform: Pose2d, xError: Double, vd: Double): Twist2d {
        val l = lookaheadTransform.translation.norm
        val curvature = 2 * lookaheadTransform.translation.y / l.pow(2)

        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return Twist2d(
                dx = adjustedLinearVelocity,
                dy = 0.0,
                dtheta = adjustedLinearVelocity * curvature
        )
    }

}