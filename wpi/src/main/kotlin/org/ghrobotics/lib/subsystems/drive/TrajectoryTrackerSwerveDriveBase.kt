package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.controller.RamseteController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import org.ghrobotics.lib.commands.FalconSubsystem

abstract class TrajectoryTrackerSwerveDriveBase: FalconSubsystem() {
    abstract val controller: RamseteController
    abstract var robotPosition: Pose2d
    abstract val kinematics: SwerveDriveKinematics

    abstract fun setOutputSI(
        driveVelocity: Double,
        driveAcceleration: Double,
        leftFrontAngle: Double,
        rightFrontAngle: Double,
        leftBackAngle: Double,
        rightBackAngle: Double,
    )
}