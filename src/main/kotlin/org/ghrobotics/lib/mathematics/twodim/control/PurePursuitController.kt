package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.Time
import kotlin.math.pow

class PurePursuitController(
    drive: DifferentialDrive,
    private val kLat: Double,
    private val kLookaheadTime: Time
) : TrajectoryFollower(drive) {

    override fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState {

        val lookaheadState = iterator.preview(kLookaheadTime)
        val lookaheadTransform = lookaheadState.state.state.pose inFrameOfReferenceOf robotPose
        val xError = (referencePose inFrameOfReferenceOf robotPose).translation._x
        val vd = referencePoint.state._velocity

        val l = lookaheadTransform.translation._norm
        val curvature = 2 * lookaheadTransform.translation._y / l.pow(2)

        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return DifferentialDrive.ChassisState(
            linear = adjustedLinearVelocity,
            angular = adjustedLinearVelocity * curvature
        )
    }
}