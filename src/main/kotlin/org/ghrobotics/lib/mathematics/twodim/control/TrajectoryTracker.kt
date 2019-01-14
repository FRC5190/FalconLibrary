package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.Trajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.AngularVelocity
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearVelocity
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.subsystems.drive.TrajectoryTrackerOutput
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Follows a smooth trajectory.
 */
abstract class TrajectoryTracker {

    private var trajectoryIterator: TrajectoryIterator<Time, TimedEntry<Pose2dWithCurvature>>? = null
    private var deltaTimeController = DeltaTime()
    private var previousVelocity: TrajectoryTrackerVelocityOutput? = null

    val referencePoint get() = trajectoryIterator?.currentState
    val isFinished get() = trajectoryIterator?.isDone ?: true

    fun reset(trajectory: Trajectory<Time, TimedEntry<Pose2dWithCurvature>>) {
        trajectoryIterator = trajectory.iterator()
        deltaTimeController.reset()
        previousVelocity = null
    }

    fun nextState(
        currentRobotPose: Pose2d,
        currentTime: Time = System.currentTimeMillis().millisecond
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
                _linearVelocity = velocity._linearVelocity,
                _linearAcceleration = 0.0,
                _angularVelocity = velocity._angularVelocity,
                _angularAcceleration = 0.0
            )
        } else {
            TrajectoryTrackerOutput(
                _linearVelocity = velocity._linearVelocity,
                _linearAcceleration = (velocity._linearVelocity - previousVelocity._linearVelocity) / deltaTime.value,
                _angularVelocity = velocity._angularVelocity,
                _angularAcceleration = (velocity._angularVelocity - previousVelocity._angularVelocity) / deltaTime.value
            )
        }
    }

    protected abstract fun calculateState(
        iterator: TrajectoryIterator<Time, TimedEntry<Pose2dWithCurvature>>,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput

    protected data class TrajectoryTrackerVelocityOutput internal constructor(
        internal val _linearVelocity: Double,
        internal val _angularVelocity: Double
    ) {
        constructor(
            linearVelocity: LinearVelocity,
            angularVelocity: AngularVelocity
        ) : this(
            _linearVelocity = linearVelocity.value,
            _angularVelocity = angularVelocity.value
        )
    }

}


