package org.ghrobotics.lib.subsystems.drive

import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.commands.FalconSubsystem
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
    driveSubsystem: FalconSubsystem,
    private val driveBase: FollowerDriveBase,
    val trajectorySource: Source<TimedTrajectory<Pose2dWithCurvature>>
) : FalconCommand(driveSubsystem) {

    /**
     * Shortcut for [TankDriveSubsystem]
     */
    constructor(
        driveSubsystem: TankDriveSubsystem,
        trajectorySource: Source<TimedTrajectory<Pose2dWithCurvature>>
    ) : this(driveSubsystem, driveSubsystem, trajectorySource)

    /**
     * Secondary constructor to directly pass in a trajectory without a source.
     */
    constructor(
        driveSubsystem: FalconSubsystem,
        driveBase: FollowerDriveBase,
        trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) : this(driveSubsystem, driveBase, Source(trajectory))

    /**
     * Secondary constructor for [TankDriveSubsystem] to directly pass in a trajectory without a source.
     */
    constructor(
        driveSubsystem: TankDriveSubsystem,
        trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) : this(driveSubsystem, driveSubsystem, Source(trajectory))

    private var trajectoryFinished = false

    /**
     * Retrieve the trajectory follower from the drive subsystem.
     */
    private val trajectoryFollower get() = driveBase.trajectoryFollower

    init {
        finishCondition += { trajectoryFinished }
    }

    /**
     * Reset the trajectory follower with the new trajectory.
     */
    override suspend fun initialize() {
        trajectoryFollower.resetTrajectory(trajectorySource())
        trajectoryFinished = false
        LiveDashboard.isFollowingPath = true
    }

    /**
     * Get the robot position, update the follower and get the desired velocities and set outputs
     * to the drivetrain.
     */
    override suspend fun execute() {
        // Get the robot position from odometry.
        val robotPosition = driveBase.localization()

        // Get the trajectory follower output.
        val output = trajectoryFollower.getOutputFromDynamics(robotPosition)

        // Update Current Path Location on Live Dashboard
        LiveDashboard.pathX = trajectoryFollower.referencePose.translation.x.feet
        LiveDashboard.pathY = trajectoryFollower.referencePose.translation.y.feet
        LiveDashboard.pathHeading = trajectoryFollower.referencePose.rotation.radian

        // Set outputs
        driveBase.leftMotor.setVelocityAndArbitraryFeedForward(
            output.leftSetPoint,
            output.leftVoltage.value / 12.0
        )

        driveBase.rightMotor.setVelocityAndArbitraryFeedForward(
            output.rightSetPoint,
            output.rightVoltage.value / 12.0
        )

        trajectoryFinished = trajectoryFollower.isFinished
    }

    /**
     * Make sure that the drivetrain is stopped at the end of the command.
     */
    override suspend fun dispose() {
        driveBase.zeroOutputs()
        LiveDashboard.isFollowingPath = false
    }
}