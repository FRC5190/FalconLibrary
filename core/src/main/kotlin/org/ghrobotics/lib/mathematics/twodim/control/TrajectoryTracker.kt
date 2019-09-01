/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.AngularAcceleration
import org.ghrobotics.lib.mathematics.units.derived.AngularVelocity
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.acceleration
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Base class for building different types of trajectory trackers.
 */
abstract class TrajectoryTracker {

    private var trajectory: Trajectory? = null
    private var deltaTimeController = DeltaTime()
    private var previousVelocity: TrajectoryTrackerVelocityOutput? = null

    val referencePoint get() = trajectory?.currentState
    val isFinished get() = trajectory?.isDone ?: true

    /**
     * Resets the tracker with the new trajectory.
     */
    fun reset(trajectory: Trajectory) {
        this.trajectory = trajectory
        deltaTimeController.reset()
        previousVelocity = null
    }

    /**
     * Returns the next output for the drivetrain.
     *
     * @param currentRobotPose The current robot pose.
     * @param currentTime The current time.
     */
    fun nextState(
        currentRobotPose: Pose2d,
        currentTime: SIUnit<Second> = System.currentTimeMillis().toDouble().milli.seconds
    ): TrajectoryTrackerOutput {
        val iterator = trajectory
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
                linearAcceleration = 0.0.meters.acceleration,
                angularVelocity = velocity.angularVelocity,
                angularAcceleration = 0.0.radians.acceleration
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

    /**
     * Calculates the new state based on the current trajectory position
     * and the robot pose.
     *
     * @param trajectory The current trajectory.
     * @param robotPose The current robot pose.
     *
     * @return The trajectory tracker's velocity output (linear and angular).
     */
    protected abstract fun calculateState(
        trajectory: Trajectory,
        robotPose: Pose2d
    ): TrajectoryTrackerVelocityOutput

    /**
     * A velocity output from the trajectory tracker.
     *
     * @param linearVelocity The commanded linear velocity.
     * @param angularVelocity The commanded angular velocity.
     */
    protected data class TrajectoryTrackerVelocityOutput constructor(
        val linearVelocity: SIUnit<LinearVelocity>,
        val angularVelocity: SIUnit<AngularVelocity>
    )
}

/**
 * Represents the output from the trajectory tracker.
 *
 * @param linearVelocity The commanded linear velocity.
 * @param linearAcceleration The commanded linear acceleration.
 * @param angularVelocity The commanded angular velocity.
 * @param angularAcceleration The commanded angular acceleration.
 */
data class TrajectoryTrackerOutput(
    val linearVelocity: SIUnit<LinearVelocity>,
    val linearAcceleration: SIUnit<LinearAcceleration>,
    val angularVelocity: SIUnit<AngularVelocity>,
    val angularAcceleration: SIUnit<AngularAcceleration>
) {
    /**
     * Returns the linear and angular velocity through a chassis speeds object.
     */
    val chassisSpeeds
        get() = ChassisSpeeds(
            linearVelocity.value, 0.0, angularVelocity.value
        )

    /**
     * Returns the linear and angular acceleration through a chassis speeds object,
     */
    val chassisAccelerations
        get() = ChassisSpeeds(linearAcceleration.value, 0.0, angularAcceleration.value)
}


