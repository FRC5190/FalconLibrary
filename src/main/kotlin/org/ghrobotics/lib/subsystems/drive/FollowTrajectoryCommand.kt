package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.commands.TimedFalconCommand
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableVariable

class FollowTrajectoryCommand(
        val driveSubsystem: TankDriveSubsystem,
        val trajectorySource: Source<TimedTrajectory<Pose2dWithCurvature>>
) : TimedFalconCommand(driveSubsystem) {

    constructor(
            driveSubsystem: TankDriveSubsystem,
            trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) : this(driveSubsystem, Source(trajectory))

    private val trajectoryFinished = ObservableVariable(false)

    private val trajectoryFollower = driveSubsystem.trajectoryFollower

    override fun CreateCommandScope.create() {
        finishCondition += trajectoryFinished
    }

    override suspend fun InitCommandScope.initialize() {
        trajectoryFollower.resetTrajectory(trajectorySource())
        trajectoryFinished.value = false
    }

    override suspend fun ExecuteCommandScope.timedExecute() {
        val robotPosition = driveSubsystem.localization.robotPosition

        val output = trajectoryFollower.getOutputFromDynamics(robotPosition)

        driveSubsystem.leftMaster.set(
                ControlMode.Velocity,
                output.lSetpoint,
                DemandType.ArbitraryFeedForward,
                output.lfVoltage.value / 12.0
        )

        driveSubsystem.rightMaster.set(
                ControlMode.Velocity,
                output.rSetpoint,
                DemandType.ArbitraryFeedForward,
                output.rfVoltage.value / 12.0
        )

        trajectoryFinished.value = trajectoryFollower.isFinished
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }

}