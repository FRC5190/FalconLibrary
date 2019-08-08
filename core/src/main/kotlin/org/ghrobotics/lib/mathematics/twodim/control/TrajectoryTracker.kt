package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.Trajectory
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Second
import org.ghrobotics.lib.mathematics.units2.derived.AngularAcceleration
import org.ghrobotics.lib.mathematics.units2.derived.AngularVelocity
import org.ghrobotics.lib.mathematics.units2.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units2.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units2.derived.acceleration
import org.ghrobotics.lib.mathematics.units2.derived.radian
import org.ghrobotics.lib.mathematics.units2.meter
import org.ghrobotics.lib.mathematics.units2.milli
import org.ghrobotics.lib.mathematics.units2.operations.div
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Follows a smooth trajectory.
 */
abstract class TrajectoryTracker {

    private var trajectoryIterator: TrajectoryIterator<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>? = null
    private var deltaTimeController = DeltaTime()
    private var previousVelocity: TrajectoryTrackerVelocityOutput? = null

    val referencePoint get() = trajectoryIterator?.currentState
    val isFinished get() = trajectoryIterator?.isDone ?: true

    fun reset(trajectory: Trajectory<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>) {
        trajectoryIterator = trajectory.iterator()
        deltaTimeController.reset()
        previousVelocity = null
    }

    fun nextState(
        currentRobotPose: Pose2d,
        currentTime: SIUnit<Second> = System.currentTimeMillis().toDouble().milli.second
    ): TrajectoryTrackerOutput {
        val iterator = trajectoryIterator
        require(iterator != null) {
            "You cannot get the next state from the TrajectoryTracker without a trajectory! Call TrajectoryTracker#reset first!"
        }
        val deltaTime = deltaTimeController.updateTime(currentTime)
        iterator.advance(deltaTime)

        val velocity = calculateState(iterator, currentRobotPose)
        val previousVelocity = this.previousVelocity
        this.previousVelocity = velocity

        // Calculate Acceleration (useful for drive dynamics)
        return if (previousVelocity == null || deltaTime.value <= 0) {
            TrajectoryTrackerOutput(
                linearVelocity = velocity.linearVelocity,
                linearAcceleration = 0.0.meter.acceleration,
                angularVelocity = velocity.angularVelocity,
                angularAcceleration = 0.0.radian.acceleration
            )
        } else {
            TrajectoryTrackerOutput(
                linearVelocity = velocity.linearVelocity,
                linearAcceleration = (velocity.linearVelocity - previousVelocity.linearVelocity) / deltaTime,
                angularVelocity = velocity.angularVelocity,
                angularAcceleration = (velocity.angularVelocity - previousVelocity.angularVelocity) / deltaTime
            )
        }
    }

    protected abstract fun calculateState(
        iterator: TrajectoryIterator<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput

    protected data class TrajectoryTrackerVelocityOutput constructor(
        val linearVelocity: SIUnit<LinearVelocity>,
        val angularVelocity: SIUnit<AngularVelocity>
    )
}

data class TrajectoryTrackerOutput constructor(
    val linearVelocity: SIUnit<LinearVelocity>,
    val linearAcceleration: SIUnit<LinearAcceleration>,
    val angularVelocity: SIUnit<AngularVelocity>,
    val angularAcceleration: SIUnit<AngularAcceleration>
) {

    val differentialDriveVelocity
        get() = DifferentialDrive.ChassisState(
            linearVelocity.value,
            angularVelocity.value
        )

    val differentialDriveAcceleration
        get() = DifferentialDrive.ChassisState(
            linearAcceleration.value,
            angularAcceleration.value
        )
}


