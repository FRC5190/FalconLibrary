/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.Notifier
import edu.wpi.first.wpilibj.experimental.command.WaitUntilCommand
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.localization.Localization
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.withSign

/**
 * Represents a standard tank drive subsystem
 */
abstract class TankDriveSubsystem : FalconSubsystem(),
    DifferentialTrackerDriveBase {

    private var quickStopAccumulator = 0.0

    abstract val localization: Localization

    override var robotPosition
        get() = localization.robotPosition
        set(value) = localization.reset(value)

    override fun lateInit() {
        // Ensure localization starts at (0,0)
        localization.reset()
        // Start a notifier loop to constantly update localization at 100hz
        Notifier { localization.update() }.startPeriodic(1.0 / 100.0)
    }

    /**
     * Zero the outputs of the drivetrain.
     */
    override fun zeroOutputs() {
        tankDrive(0.0, 0.0)
    }

    override fun periodic() {
        // Report new position to Live Dashboard
        LiveDashboard.robotHeading = robotPosition.rotation.radians
        LiveDashboard.robotX = robotPosition.translation.x_u.inFeet()
        LiveDashboard.robotY = robotPosition.translation.y_u.inFeet()
    }

    // COMMON DRIVE TYPES

    /**
     * Arcade drive control
     */
    fun arcadeDrive(
        linearPercent: Double,
        rotationPercent: Double
    ) {
        val maxInput = max(linearPercent.absoluteValue, rotationPercent.absoluteValue)
            .withSign(linearPercent)

        val leftMotorOutput: Double
        val rightMotorOutput: Double

        if (linearPercent >= 0.0) {
            // First quadrant, else second quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            } else {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            } else {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            }
        }

        tankDrive(leftMotorOutput, rightMotorOutput)
    }

    /**
     * Curvature or cheezy drive control
     */
    @Suppress("ComplexMethod")
    fun curvatureDrive(
        linearPercent: Double,
        curvaturePercent: Double,
        isQuickTurn: Boolean
    ) {
        val angularPower: Double
        val overPower: Boolean

        if (isQuickTurn) {
            if (linearPercent.absoluteValue < kQuickStopThreshold) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator +
                    kQuickStopAlpha * curvaturePercent.coerceIn(-1.0, 1.0) * 2.0
            }
            overPower = true
            angularPower = curvaturePercent
        } else {
            overPower = false
            angularPower = linearPercent.absoluteValue * curvaturePercent - quickStopAccumulator

            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1.0
                quickStopAccumulator < -1 -> quickStopAccumulator += 1.0
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = linearPercent + angularPower
        var rightMotorOutput = linearPercent - angularPower

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (overPower) {
            when {
                leftMotorOutput > 1.0 -> {
                    rightMotorOutput -= leftMotorOutput - 1.0
                    leftMotorOutput = 1.0
                }
                rightMotorOutput > 1.0 -> {
                    leftMotorOutput -= rightMotorOutput - 1.0
                    rightMotorOutput = 1.0
                }
                leftMotorOutput < -1.0 -> {
                    rightMotorOutput -= leftMotorOutput + 1.0
                    leftMotorOutput = -1.0
                }
                rightMotorOutput < -1.0 -> {
                    leftMotorOutput -= rightMotorOutput + 1.0
                    rightMotorOutput = -1.0
                }
            }
        }

        // Normalize the wheel speeds
        val maxMagnitude = max(leftMotorOutput.absoluteValue, rightMotorOutput.absoluteValue)
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        tankDrive(leftMotorOutput, rightMotorOutput)
    }


    /**
     * Tank drive control
     */
    open fun tankDrive(
        leftPercent: Double,
        rightPercent: Double
    ) {
        leftMotor.setDutyCycle(leftPercent.coerceIn(-1.0, 1.0))
        rightMotor.setDutyCycle(rightPercent.coerceIn(-1.0, 1.0))
    }


    // PRE GENERATED TRAJECTORY METHODS

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     */
    fun followTrajectory(
        trajectory: Trajectory,
        dt: SIUnit<Second> = 20.milli.seconds
    ) = TrajectoryTrackerCommand(this, this, { trajectory }, dt = dt)

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     * @param pathMirrored Whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Trajectory,
        pathMirrored: Boolean = false,
        dt: SIUnit<Second> = 20.milli.seconds
    ) = followTrajectory(trajectory.let {
        if (pathMirrored) it.mirror() else it
    }, dt)

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory Source with the trajectory to follow
     * @param pathMirrored Whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Source<Trajectory>,
        pathMirrored: Boolean = false,
        dt: SIUnit<Second> = 20.milli.seconds
    ) = TrajectoryTrackerCommand(this, this, trajectory.map {
        if (pathMirrored) it.mirror() else it
    }, dt = dt)

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     * @param pathMirrored Source with whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Trajectory,
        pathMirrored: BooleanSource,
        dt: SIUnit<Second> = 20.milli.seconds
    ) = followTrajectory(pathMirrored.map(trajectory.mirror(), trajectory), dt = dt)

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory Source with yhe trajectory to follow
     * @param pathMirrored Source with whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Source<Trajectory>,
        pathMirrored: BooleanSource,
        dt: SIUnit<Second> = 20.milli.seconds
    ) = followTrajectory(pathMirrored.map(trajectory.map { it.mirror() }, trajectory), dt = dt)


    // REGIONAL CONDITIONAL COMMAND METHODS

    /**
     * Returns a condition command that checks if the robot is in a specified region
     *
     * @param region The region to check if the robot is in.
     */
    fun withinRegion(region: Rectangle2d) =
        withinRegion(Source(region))


    /**
     * Returns a condition command that checks if the robot is in a specified region
     *
     * @param region Source with the region to check if the robot is in.
     */
    fun withinRegion(region: Source<Rectangle2d>) =
        WaitUntilCommand { region().contains(robotPosition.translation) }

    companion object {
        const val kQuickStopThreshold = edu.wpi.first.wpilibj.drive.DifferentialDrive.kDefaultQuickStopThreshold
        const val kQuickStopAlpha = edu.wpi.first.wpilibj.drive.DifferentialDrive.kDefaultQuickStopAlpha
    }
}