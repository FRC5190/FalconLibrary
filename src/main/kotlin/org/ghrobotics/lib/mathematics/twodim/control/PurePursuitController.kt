package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.DeltaTime
import kotlin.math.pow

class PurePursuitController(
    trajectory: TimedTrajectory<Pose2dWithCurvature>,
    private val kLat: Double,
    private val kLookaheadTime: Time
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
            lookaheadTransform = iterator.preview(kLookaheadTime).state.state.pose inFrameOfReferenceOf robot,
            xError = (pose inFrameOfReferenceOf robot).translation.xRaw,
            vd = point.state.velocity
        ).also { point = iterator.advance(dt) }
    }

    private fun calculateTwist(lookaheadTransform: Pose2d, xError: Double, vd: Double): Twist2d {
        val l = lookaheadTransform.translation.norm
        val curvature = 2 * lookaheadTransform.translation.yRaw / l.pow(2)

        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return Twist2d(
            dxRaw = adjustedLinearVelocity,
            dyRaw = 0.0,
            dThetaRaw = adjustedLinearVelocity * curvature
        )
    }

}