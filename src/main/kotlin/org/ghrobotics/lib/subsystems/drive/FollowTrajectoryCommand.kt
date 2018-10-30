package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.commands.TimedFalconCommand
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableVariable

class FollowTrajectoryCommand(
        val driveSubsystem: TankDriveSubsystem,
        val trajectory: TimedTrajectory<Pose2dWithCurvature>
) : TimedFalconCommand(driveSubsystem) {

    private val markers = mutableListOf<Pair<ObservableVariable<Boolean>, Time>>()

    private val trajectorySamples = mutableListOf<Pair<Translation2d, Time>>()

    fun addMarkerAt(location: Translation2d): ObservableValue<Boolean> {
        if (trajectorySamples.isEmpty()) {
            // Sample the used trajectory to find the best times for the markers
            val usedDeltaTime = (1.0 / executeFrequency).second
            val sampleIterator = trajectory.iterator()
            while (!sampleIterator.isDone) {
                val timedEntry = sampleIterator.advance(usedDeltaTime).state
                trajectorySamples += timedEntry.state.pose.translation to timedEntry.t.second
            }
        }
        return ObservableVariable(false).apply {
            markers += this to trajectorySamples.minBy { location.distance(it.first) }!!.second
        }
    }

    // Running specific variables

    private val trajectoryFinished = ObservableVariable(false)

    private val trajectoryFollower = driveSubsystem.trajectoryFollower

    override fun CreateCommandScope.create() {
        finishCondition += trajectoryFinished
    }

    override suspend fun InitCommandScope.initialize() {
        trajectorySamples.clear()

        trajectoryFollower.resetTrajectory(trajectory)
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

        // Update marker states
        markers.forEach { (hasPassed, timeRequired) ->
            hasPassed.value = timeElapsed >= timeRequired
        }

        trajectoryFinished.value = trajectoryFollower.isFinished
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }

}