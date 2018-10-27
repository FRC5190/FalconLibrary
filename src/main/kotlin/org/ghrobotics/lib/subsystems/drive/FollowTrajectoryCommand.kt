package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import edu.wpi.first.wpilibj.smartdashboard.SendableBuilder
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryFollower
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.mirror
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableVariable
import org.ghrobotics.lib.commands.TimedFalconCommand

class FollowTrajectoryCommand(
    val driveSubsystem: TankDriveSubsystem,
    val trajectoryFollower: TrajectoryFollower,
    val trajectorySource: Source<TimedTrajectory<Pose2dWithCurvature>>,
    val pathMirroredSource: BooleanSource = Source(false)
) : TimedFalconCommand(driveSubsystem) {

    constructor(
        driveSubsystem: TankDriveSubsystem,
        trajectoryFollower: TrajectoryFollower,
        trajectory: TimedTrajectory<Pose2dWithCurvature>,
        pathMirrored: Boolean = false
    ) : this(driveSubsystem, trajectoryFollower, trajectory, Source(pathMirrored))

    constructor(
        driveSubsystem: TankDriveSubsystem,
        trajectoryFollower: TrajectoryFollower,
        trajectory: TimedTrajectory<Pose2dWithCurvature>,
        pathMirrored: BooleanSource = Source(false)
    ) : this(driveSubsystem, trajectoryFollower, Source(trajectory), pathMirrored)

    private val markers = mutableListOf<Pair<ObservableVariable<Boolean>, Source<Translation2d>>>()
    private val activeMarkers = mutableListOf<Pair<ObservableVariable<Boolean>, Time>>()

    fun addMarkerAt(location: Translation2d) = addMarkerAt(Source(location))
    fun addMarkerAt(locationSource: Source<Translation2d>): ObservableValue<Boolean> =
        ObservableVariable(false).apply { markers += this to locationSource }

    // Running specific variables

    private val trajectoryFinished = ObservableVariable(false)

    lateinit var trajectoryUsed: TimedTrajectory<Pose2dWithCurvature>
        private set

    override fun CreateCommandScope.create() {
        finishCondition += trajectoryFinished
    }

    override suspend fun InitCommandScope.initialize() {
        trajectoryUsed = if (pathMirroredSource.value) trajectorySource.value.mirror() else trajectorySource.value
        trajectoryFollower.init(trajectoryUsed)

        // Sample the used trajectory to find the best times for the markers
        val usedDeltaTime = (1.0 / executeFrequency).second
        val trajectorySamples = mutableListOf<TimedEntry<Pose2dWithCurvature>>()

        val sampleIterator = trajectoryUsed.iterator()
        while (!sampleIterator.isDone) {
            trajectorySamples += sampleIterator.advance(usedDeltaTime).state
        }

        activeMarkers.clear()
        activeMarkers += markers.map { (hasPassed, locationSource) ->
            hasPassed.value = false // make sure its false

            val usedLocation = locationSource.value
            hasPassed to trajectorySamples.minBy { usedLocation.distance(it.state.pose.translation) }!!.t.second
        }

        trajectoryFinished.value = false
    }

    override suspend fun ExecuteCommandScope.timedExecute() {
        val robotPosition = driveSubsystem.localization.robotPosition

        val output = trajectoryFollower.getOutputFromDynamics(robotPosition)

        driveSubsystem.leftMaster.set(
            ControlMode.Velocity,
            output.lSetpoint,
            DemandType.ArbitraryFeedForward,
            output.lfVoltage.asDouble / 12.0
        )

        driveSubsystem.rightMaster.set(
            ControlMode.Velocity,
            output.rSetpoint,
            DemandType.ArbitraryFeedForward,
            output.rfVoltage.asDouble / 12.0
        )

        // Update marker states
        activeMarkers.forEach { (hasPassed, timeRequired) ->
            hasPassed.value = timeElapsed >= timeRequired
        }

        trajectoryFinished.value = trajectoryFollower.isFinished
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }

}