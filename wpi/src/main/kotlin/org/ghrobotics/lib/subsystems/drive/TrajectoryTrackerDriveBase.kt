package org.ghrobotics.lib.subsystems.drive

import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTrackerOutput
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.motors.LinearFalconMotor

/**
 * Just implement this if you want to use the TrajectoryTrackerCommand.
 */
interface TrajectoryTrackerDriveBase {
    val leftMotor: LinearFalconMotor
    val rightMotor: LinearFalconMotor

    val trajectoryTracker: TrajectoryTracker

    val robotPosition: Pose2d

    fun setOutput(output: TrajectoryTrackerOutput)

    @JvmDefault
    fun zeroOutputs() {
        leftMotor.setNeutral()
        rightMotor.setNeutral()
    }
}