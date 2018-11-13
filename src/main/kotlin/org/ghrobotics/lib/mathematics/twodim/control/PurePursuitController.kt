package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.*
import kotlin.math.pow


/**
 * Uses an adaptive pure pursuit controller to steer the robot back onto the desired trajectory.
 * From https://www.ri.cmu.edu/pub_files/pub3/coulter_r_craig_1992_1/coulter_r_craig_1992_1.pdf
 *
 * @param drive Instance of the differential drive that represents the dynamics of the drivetrain.
 * @param kLat Constant of latitude error. Increase this for more aggressive velocity correction if the robot falls behind.
 * @param kLookaheadTime Constant for lookahead time. Larger values mean slower but more stable convergence.
 * @param kMinLookaheadDistance Constant for minimum lookahead distance. Should help with more stability, especially toward the end of
 * the path.
 */
class PurePursuitController(
    drive: DifferentialDrive,
    private val kLat: Double,
    private val kLookaheadTime: Time,
    private val kMinLookaheadDistance: Length = 1.meter
) : TrajectoryFollower(drive) {

    /**
     * Calculate desired chassis velocity using pure pursuit.
     */
    override fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState {

        // Compute the lookahead state.
        val lookaheadState: Pose2d = iterator.preview(kLookaheadTime).state.state.pose.let {

            // The lookahead point is farther from the robot than the minimum lookahead distance.
            // Therefore we can use this point.
            if ((it inFrameOfReferenceOf robotPose).translation._norm >= kMinLookaheadDistance.value) {
                it
            }

            // We need to find a point that is at least the minimum lookahead distance away.
            else {
                var lookaheadPose = iterator.currentState.state.state.pose
                var previewedTime = 0.second

                // Run the loop until a distance that is greater than the minimum lookahead distance is found or until
                // we run out of "trajectory" to search. If this happens, we will simply extend the end of the trajectory.
                while (iterator.progress > previewedTime) {
                    previewedTime += 0.02.second

                    lookaheadPose = iterator.preview(previewedTime).state.state.pose
                    val lookaheadDistance = (lookaheadPose inFrameOfReferenceOf robotPose).translation._norm

                    if (lookaheadDistance > kMinLookaheadDistance.value) {
                        return@let lookaheadPose
                    }
                }

                // Extend the trajectory.
                val remaining =
                    kMinLookaheadDistance.value - (lookaheadPose inFrameOfReferenceOf robotPose).translation._norm

                return@let lookaheadPose.transformBy(
                    Pose2d(
                        Translation2d(remaining * if (iterator.reversed) -1 else 1, 0)
                    )
                )
            }
        }

        // Find the appropriate lookahead point.
        val lookaheadTransform = (lookaheadState inFrameOfReferenceOf robotPose)

        // Calculate latitude error.
        val xError = (referencePose inFrameOfReferenceOf robotPose).translation._x

        // Calculate the velocity at the reference point.
        val vd = referencePoint.state._velocity

        // Calculate the distance from the robot to the lookahead.
        val l = lookaheadTransform.translation._norm

        // Calculate the curvature of the arc that connects the robot and the lookahead point.
        val curvature = 2 * lookaheadTransform.translation._y / l.pow(2)

        // Adjust the linear velocity to compensate for the robot lagging behind.
        val adjustedLinearVelocity = vd * lookaheadTransform.rotation.cos + kLat * xError

        return DifferentialDrive.ChassisState(
            linear = adjustedLinearVelocity,
            // v * curvature = omega
            angular = adjustedLinearVelocity * curvature
        )
    }
}
