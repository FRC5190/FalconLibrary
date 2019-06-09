package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.experimental.command.NotifierCommand
import edu.wpi.first.wpilibj.experimental.command.Subsystem
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.Trajectory
import org.ghrobotics.lib.mathematics.units.SILengthConstants
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.utils.Source

/**
 * Command to follow a smooth trajectory using a trajectory following controller
 *
 * @param driveSubsystem Instance of the drive subsystem to use
 * @param trajectorySource Source that contains the trajectory to follow.
 */
class TrajectoryTrackerCommand(
    driveSubsystem: Subsystem,
    private val driveBase: TrajectoryTrackerDriveBase,
    val trajectorySource: Source<Trajectory<Time, TimedEntry<Pose2dWithCurvature>>>,
    private val trajectoryTracker: TrajectoryTracker = driveBase.trajectoryTracker,
    val dt: Time = 20.millisecond
) : NotifierCommand(
    Runnable {
        driveBase.setOutput(trajectoryTracker.nextState(driveBase.robotPosition))

        val referencePoint = trajectoryTracker.referencePoint
        if (referencePoint != null) {
            val referencePose = referencePoint.state.state.pose

            // Update Current Path Location on Live Dashboard
            LiveDashboard.pathX = referencePose.translation.x / SILengthConstants.kFeetToMeter
            LiveDashboard.pathY = referencePose.translation.y / SILengthConstants.kFeetToMeter
            LiveDashboard.pathHeading = referencePose.rotation.radian
        }
    },
    dt.value,
    driveSubsystem
) {

    /**
     * Reset the trajectory follower with the new trajectory.
     */
    override fun initialize() {
        trajectoryTracker.reset(trajectorySource())
        LiveDashboard.isFollowingPath = true
    }

    /**
     * Make sure that the drivetrain is stopped at the end of the command.
     */
    override fun end(interrupted: Boolean) {
        driveBase.zeroOutputs()
        LiveDashboard.isFollowingPath = false
    }

    override fun isFinished() = trajectoryTracker.isFinished
}
