package org.ghrobotics.lib.subsystems.drive

import org.ghrobotics.lib.localization.Localization
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryFollower
import org.ghrobotics.lib.wrappers.LinearFalconMotor

/**
 * Just implement this if you want to use the FollowTrajectoryCommand.
 */
interface FollowerDriveBase {
    val leftMotor: LinearFalconMotor
    val rightMotor: LinearFalconMotor

    val localization: Localization

    val trajectoryFollower: TrajectoryFollower

    fun zeroOutputs() {
        leftMotor.percentOutput = 0.0
        rightMotor.percentOutput = 0.0
    }
}
