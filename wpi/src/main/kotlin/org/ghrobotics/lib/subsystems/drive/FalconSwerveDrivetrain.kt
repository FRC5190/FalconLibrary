/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveDriveOdometry
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.debug.FalconDashboard
import org.ghrobotics.lib.localization.TimePoseInterpolatableBuffer
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.twodim.trajectory.mirror
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.inFeet
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.subsystems.AbstractFalconSwerveModule
import org.ghrobotics.lib.subsystems.SensorlessCompatibleSubsystem
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map

abstract class FalconSwerveDrivetrain : TrajectoryTrackerSwerveDriveBase(), SensorlessCompatibleSubsystem {
    /**
     * The current inputs and outputs
     */
    protected val periodicIO = PeriodicIO()

    /**
     * Helper for different drive styles.
     */
    protected val driveHelper = FalconDriveHelper()

    /**
     * The odometry object that is used to calculate the robot's position
     * on the field.
     */
    abstract val odometry: SwerveDriveOdometry

    /**
     * Buffer for storing the pose over a span of time. This is useful for
     * Vision and latency compensation.
     */
    protected open val poseBuffer = TimePoseInterpolatableBuffer()

    /**
     * The left front motor
     */
    abstract val modules: Array<AbstractFalconSwerveModule>

    abstract val wheelbase: Double

    abstract val trackwidth: Double

    /**
     * The characterization for the left front swerve module.
     */
    abstract val leftFrontCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the right front swerve module.
     */
    abstract val rightFrontCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the left back swerve module.
     */
    abstract val leftBackCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the right back swerve module.
     */
    abstract val rightBackCharacterization: SimpleMotorFeedforward

    /**
     * The rotation source / gyro
     */
    abstract val gyro: Source<Rotation2d>

    /**
     * Get the robot's position on the field.
     */
    override var robotPosition: Pose2d = Pose2d()

    override fun periodic() {
        periodicIO.leftFrontVoltage = modules[0].voltageOutput
        periodicIO.rightFrontVoltage = modules[1].voltageOutput
        periodicIO.rightBackVoltage = modules[2].voltageOutput
        periodicIO.leftBackVoltage = modules[3].voltageOutput

        periodicIO.leftFrontCurrent = modules[0].drawnCurrent
        periodicIO.rightFrontCurrent = modules[1].drawnCurrent
        periodicIO.rightBackCurrent = modules[2].drawnCurrent
        periodicIO.leftBackCurrent = modules[3].drawnCurrent

        periodicIO.leftFrontPosition = modules[0].drivePosition
        periodicIO.rightFrontPosition = modules[1].drivePosition
        periodicIO.rightBackPosition = modules[2].drivePosition
        periodicIO.leftBackPosition = modules[3].drivePosition

        periodicIO.leftFrontVelocity = modules[0].driveVelocity
        periodicIO.rightFrontVelocity = modules[1].driveVelocity
        periodicIO.rightBackVelocity = modules[2].driveVelocity
        periodicIO.leftBackVelocity = modules[3].driveVelocity

        periodicIO.gyro = gyro()

        val feedForwards = Array(4) { 0.0.volts }
        feedForwards[0] = periodicIO.leftFrontFeedforward
        feedForwards[1] = periodicIO.rightFrontFeedforward
        feedForwards[2] = periodicIO.rightBackFeedforward
        feedForwards[3] = periodicIO.leftBackFeedforward

        val states: Array<SwerveModuleState> = Array(4) { SwerveModuleState() }
        for (i in 0..modules.size) {
            states[i] = modules[i].state()
        }

        robotPosition = odometry.update(
            periodicIO.gyro, *states
        )
        poseBuffer[Timer.getFPGATimestamp().seconds] = robotPosition

        when (val desiredOutput = periodicIO.desiredOutput) {
            is Output.Nothing -> {
                modules.forEach { it.setNeutral() }
            }
            is Output.Percent -> {
                for (i in 0..modules.size) {
                    modules[i].setControls(desiredOutput.speeds[i], desiredOutput.azimuths[i])
                }
            }
            is Output.States -> {
                for (i in 0..modules.size) {
                    modules[i].setState(states[i], feedForwards[i])
                }
            }
        }

        FalconDashboard.robotHeading = robotPosition.rotation.radians
        FalconDashboard.robotX = robotPosition.translation.x_u.inFeet()
        FalconDashboard.robotY = robotPosition.translation.y_u.inFeet()
    }

    /**
     * Represents periodic data
     */

    override fun lateInit() {
        resetPosition(Pose2d())
    }

    override fun setNeutral() {
        periodicIO.desiredOutput = Output.Nothing
        periodicIO.leftFrontFeedforward = 0.volts
        periodicIO.rightFrontFeedforward = 0.volts
        periodicIO.leftBackFeedforward = 0.volts
        periodicIO.rightBackFeedforward = 0.volts
    }

    override fun setOutputSI(
        states: Array<SwerveModuleState>
    ) {
        periodicIO.desiredOutput = Output.States(states)
    }

    fun getPose(timestamp: SIUnit<Second> = Timer.getFPGATimestamp().seconds): Pose2d {
        return poseBuffer[timestamp] ?: run {
            DriverStation.reportError("[FalconWCD] Pose Buffer is Empty!", false)
            Pose2d()
        }
    }

    fun swerveDrive(forwardInput: Double, strafeInput: Double, rotationInput: Double, fieldRelative: Boolean) {
        val signals = driveHelper.swerveDrive(this, forwardInput, strafeInput, rotationInput, fieldRelative)
        periodicIO.desiredOutput = Output.Percent(signals.wheelSpeeds, signals.wheelAzimuths)
    }

    fun resetPosition(pose: Pose2d) {
        modules.forEach { it.resetDriveEncoder(0.meters) }
        odometry.resetPosition(pose, gyro())
    }

    fun followTrajectory(trajectory: Trajectory, mirrored: Boolean = false) =
        SwerveTrajectoryTrackerCommand(this, Source(if (mirrored) trajectory.mirror() else trajectory))

    fun followTrajectory(trajectory: Trajectory, mirrored: BooleanSource) =
        SwerveTrajectoryTrackerCommand(this, mirrored.map(trajectory.mirror(), trajectory))

    fun followTrajectory(trajectory: Source<Trajectory>) =
        SwerveTrajectoryTrackerCommand(this, trajectory)

    protected class PeriodicIO {
        var leftFrontVoltage: SIUnit<Volt> = 0.volts
        var rightFrontVoltage: SIUnit<Volt> = 0.volts
        var rightBackVoltage: SIUnit<Volt> = 0.volts
        var leftBackVoltage: SIUnit<Volt> = 0.volts

        var leftFrontCurrent: SIUnit<Ampere> = 0.amps
        var rightFrontCurrent: SIUnit<Ampere> = 0.amps
        var rightBackCurrent: SIUnit<Ampere> = 0.amps
        var leftBackCurrent: SIUnit<Ampere> = 0.amps

        var leftFrontPosition: SIUnit<Meter> = 0.meters
        var rightFrontPosition: SIUnit<Meter> = 0.meters
        var rightBackPosition: SIUnit<Meter> = 0.meters
        var leftBackPosition: SIUnit<Meter> = 0.meters

        var leftFrontVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var rightFrontVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var rightBackVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var leftBackVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds

        var gyro: Rotation2d = Rotation2d()

        var desiredOutput: Output = Output.Nothing

        var leftFrontFeedforward: SIUnit<Volt> = 0.volts
        var rightFrontFeedforward: SIUnit<Volt> = 0.volts
        var rightBackFeedforward: SIUnit<Volt> = 0.volts
        var leftBackFeedforward: SIUnit<Volt> = 0.volts
    }
    /**
     * Represents the typical outputs for the drivetrain.
     */
    protected sealed class Output {
        // No outputs
        object Nothing : Output()

        // Percent Output
        class Percent(val speeds: DoubleArray, val azimuths: Array<org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d>) : Output()

        class States(val states: Array<SwerveModuleState>) : Output()
    }
}
