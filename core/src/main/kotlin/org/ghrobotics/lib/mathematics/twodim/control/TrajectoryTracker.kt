/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.*
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Follows a smooth trajectory.
 */
abstract class TrajectoryTracker {

    private var trajectory: Trajectory? = null
    private var deltaTimeController = DeltaTime()
    private var previousVelocity: TrajectoryTrackerVelocityOutput? = null

    val referencePoint get() = trajectory?.currentState
    val isFinished get() = trajectory?.isDone ?: true

    fun reset(trajectory: Trajectory) {
        this.trajectory = trajectory
        deltaTimeController.reset()
        previousVelocity = null
    }

    fun nextState(
        currentRobotPose: Pose2d,
        currentTime: SIUnit<Second> = System.currentTimeMillis().toDouble().milli.second
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
        trajectory: Trajectory,
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


