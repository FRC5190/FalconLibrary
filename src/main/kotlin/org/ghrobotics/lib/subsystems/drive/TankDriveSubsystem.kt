package org.ghrobotics.lib.subsystems.drive

import org.ghrobotics.lib.commands.ConditionCommand
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryFollower
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.mirror
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.sensors.AHRSSensor
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map
import org.ghrobotics.lib.wrappers.FalconSRX

abstract class TankDriveSubsystem : FalconSubsystem("Drive Subsystem") {
    abstract val leftMaster: FalconSRX<Length>
    abstract val rightMaster: FalconSRX<Length>

    abstract val ahrsSensor: AHRSSensor
    abstract val trajectoryFollower: TrajectoryFollower

    @Suppress("LeakingThis")
    val localization = TankDriveLocalization(this)

    // Pre-generated Trajectory Methods

    fun followTrajectory(
            trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) = FollowTrajectoryCommand(this, trajectory)

    fun followTrajectory(
            trajectory: TimedTrajectory<Pose2dWithCurvature>,
            pathMirrored: Boolean = false
    ) = followTrajectory(trajectory.let {
        if (pathMirrored) it.mirror() else it
    })

    fun followTrajectory(
            trajectory: Source<TimedTrajectory<Pose2dWithCurvature>>,
            pathMirrored: Boolean = false
    ) = FollowTrajectoryCommand(this, trajectory.map {
        if (pathMirrored) it.mirror() else it
    })

    fun followTrajectory(
            trajectory: TimedTrajectory<Pose2dWithCurvature>,
            pathMirrored: BooleanSource
    ) = followTrajectory(pathMirrored.map(trajectory.mirror(), trajectory))

    fun followTrajectory(
            trajectory: Source<TimedTrajectory<Pose2dWithCurvature>>,
            pathMirrored: BooleanSource
    ) = followTrajectory(pathMirrored.map(trajectory.map { it.mirror() }, trajectory))


    // Region conditional command methods

    fun withinRegion(region: Rectangle2d) =
            withinRegion(Source(region))

    fun withinRegion(region: Source<Rectangle2d>) =
            ConditionCommand { region().contains(localization.robotPosition.translation) }

}