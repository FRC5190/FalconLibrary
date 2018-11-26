package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.utils.Source

/**
 * Command to follow a smooth trajectory using a trajectory following controller
 *
 * @param driveSubsystem Instance of the drive subsystem to use
 * @param trajectorySource Source that contains the trajectory to follow.
 */
class FollowTrajectoryCommand(
    val driveSubsystem: TankDriveSubsystem,
    val trajectorySource: Source<TimedTrajectory<Pose2dWithCurvature>>
) : FalconCommand(driveSubsystem) {

    /**
     * Secondary constructor to directly pass in a trajectory without a source.
     */
    constructor(
        driveSubsystem: TankDriveSubsystem,
        trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) : this(driveSubsystem, Source(trajectory))

    private var trajectoryFinished = false

    /**
     * Retrieve the trajectory follower from the drive subsystem.
     */
    private val trajectoryFollower = driveSubsystem.trajectoryFollower

    init {
        finishCondition += { trajectoryFinished }
    }

    /**
     * Reset the trajectory follower with the new trajectory.
     */
    override suspend fun initialize() {
        trajectoryFollower.resetTrajectory(trajectorySource())
        trajectoryFinished = false

        // Reset Path on Live Dashboard
        LiveDashboard.pathReset = true
    }

    /**
     * Get the robot position, update the follower and get the desired velocities and set outputs
     * to the drivetrain.
     */
    override suspend fun execute() {
        // Get the robot position from odometry.
        val robotPosition = driveSubsystem.localization.robotPosition

        // Get the trajectory follower output.
        val output = trajectoryFollower.getOutputFromDynamics(robotPosition)

        // Update Current Path Location on Live Dashboard
        LiveDashboard.pathX = trajectoryFollower.referencePose.translation.x.feet
        LiveDashboard.pathY = trajectoryFollower.referencePose.translation.y.feet
        LiveDashboard.pathHeading = trajectoryFollower.referencePose.rotation.degree

        // Set outputs
        driveSubsystem.leftMaster.set(
            ControlMode.Velocity,
            output.leftSetPoint,
            DemandType.ArbitraryFeedForward,
            output.leftVoltage.value / 12.0
        )

        driveSubsystem.rightMaster.set(
            ControlMode.Velocity,
            output.rightSetPoint,
            DemandType.ArbitraryFeedForward,
            output.rightVoltage.value / 12.0
        )

        trajectoryFinished = trajectoryFollower.isFinished
    }

    /**
     * Make sure that the drivetrain is stopped at the end of the command.
     */
    override suspend fun dispose() {
        driveSubsystem.zeroOutputs()
    }
}