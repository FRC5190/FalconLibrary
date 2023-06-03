/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.westcoast

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.DifferentialDriveOdometry
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
import org.ghrobotics.lib.motors.FalconMotor
import org.ghrobotics.lib.subsystems.SensorlessCompatibleSubsystem
import org.ghrobotics.lib.subsystems.drive.CharacterizationCommand
import org.ghrobotics.lib.subsystems.drive.FalconDriveHelper
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map

/**
 * Represents a typical west coast drive that is built by Team 5190.
 */
abstract class FalconWestCoastDrivetrain : TrajectoryTrackerWestCoastDriveBase(), SensorlessCompatibleSubsystem {
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
    abstract val odometry: DifferentialDriveOdometry

    /**
     * Buffer for storing the pose over a span of time. This is useful for
     * Vision and latency compensation.
     */
    protected open val poseBuffer = TimePoseInterpolatableBuffer()

    /**
     * The left motor
     */
    protected abstract val leftMotor: FalconMotor<Meter>

    /**
     * The right motor
     */
    protected abstract val rightMotor: FalconMotor<Meter>

    /**
     * The characterization for the left gearbox.
     */
    abstract val leftCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the right gearbox.
     */
    abstract val rightCharacterization: SimpleMotorFeedforward

    /**
     * The rotation source / gyro
     */
    abstract val gyro: Source<Rotation2d>

    /**
     * Get the robot's position on the field.
     */
    override var robotPosition: Pose2d = Pose2d()

    /**
     * Returns the voltage output of the left motor.
     */
    val leftVoltage get() = periodicIO.leftVoltage

    /**
     * Returns the voltage output of the right motor.
     */
    val rightVoltage get() = periodicIO.rightVoltage

    /**
     * Returns the position of the left side of the drivetrain.
     */
    val leftPosition get() = periodicIO.leftPosition

    /**
     * Returns the position of the right side of the drivetrain.
     */
    val rightPosition get() = periodicIO.rightPosition

    /**
     * Returns the velocity of the left side of the drivetrain.
     */
    val leftVelocity get() = periodicIO.leftVelocity

    /**
     * Returns the velocity of the right side of the drivetrain.
     */
    val rightVelocity get() = periodicIO.rightVelocity

    /**
     * Periodic function -- runs every 20 ms.
     */
    override fun periodic() {
        periodicIO.leftVoltage = leftMotor.voltageOutput
        periodicIO.rightVoltage = rightMotor.voltageOutput

        periodicIO.leftCurrent = leftMotor.drawnCurrent
        periodicIO.rightCurrent = rightMotor.drawnCurrent

        periodicIO.leftPosition = leftMotor.encoder.position
        periodicIO.rightPosition = rightMotor.encoder.position

        periodicIO.leftVelocity = leftMotor.encoder.velocity
        periodicIO.rightVelocity = rightMotor.encoder.velocity

        periodicIO.gyro = gyro()

        val leftFeedforward = periodicIO.leftFeedforward
        val rightFeedforward = periodicIO.rightFeedforward

        robotPosition = odometry.update(
            periodicIO.gyro,
            periodicIO.leftPosition.value,
            periodicIO.rightPosition.value,
        )
        poseBuffer[Timer.getFPGATimestamp().seconds] = robotPosition

        when (val desiredOutput = periodicIO.desiredOutput) {
            is Output.Nothing -> {
                leftMotor.setNeutral()
                rightMotor.setNeutral()
            }

            is Output.Percent -> {
                leftMotor.setDutyCycle(desiredOutput.left, leftFeedforward)
                rightMotor.setDutyCycle(desiredOutput.right, rightFeedforward)
            }

            is Output.Velocity -> {
                leftMotor.setVelocity(desiredOutput.left, leftFeedforward)
                rightMotor.setVelocity(desiredOutput.right, rightFeedforward)
            }
        }

        FalconDashboard.robotHeading = robotPosition.rotation.radians
        FalconDashboard.robotX = robotPosition.translation.x_u.inFeet()
        FalconDashboard.robotY = robotPosition.translation.y_u.inFeet()
    }

    /**
     * Reset pose and encoders to zero.
     */
    override fun lateInit() {
        resetPosition(Pose2d())
    }

    /**
     * Cut power to the drivetrain motors.
     */
    override fun setNeutral() {
        periodicIO.desiredOutput = Output.Nothing
        periodicIO.leftFeedforward = 0.volts
        periodicIO.rightFeedforward = 0.volts
    }

    /**
     * Set the trajectory tracker output
     */
    override fun setOutputSI(
        leftVelocity: Double,
        rightVelocity: Double,
        leftAcceleration: Double,
        rightAcceleration: Double,
    ) {
        periodicIO.leftFeedforward = SIUnit(leftCharacterization.calculate(leftVelocity, leftAcceleration))
        periodicIO.rightFeedforward = SIUnit(rightCharacterization.calculate(rightVelocity, rightAcceleration))
        periodicIO.desiredOutput = Output.Velocity(SIUnit(leftVelocity), SIUnit(rightVelocity))
    }

    /**
     * Returns the pose at the specified timestamp.
     * @param timestamp The timestamp to retrieve the pose at.
     *
     * @return The pose at the specified timestamp.
     */
    fun getPose(timestamp: SIUnit<Second> = Timer.getFPGATimestamp().seconds): Pose2d {
        return poseBuffer[timestamp] ?: {
            DriverStation.reportError("[FalconWCD] Pose Buffer is Empty!", false)
            Pose2d()
        }()
    }

    /**
     * Drives the robot using arcade drive.
     *
     * @param linearPercent The percent for linear motion.
     * @param rotationPercent The percent for angular rotation.
     */
    fun arcadeDrive(
        linearPercent: Double,
        rotationPercent: Double,
    ) {
        val (l, r) = driveHelper.arcadeDrive(linearPercent, rotationPercent)
        setPercent(l, r)
    }

    /**
     * Drives the robot using curvature drive.
     *
     * @param linearPercent The percent for linear motion.
     * @param curvaturePercent The percent for curvature of the robot.
     * @param isQuickTurn Whether to use arcade drive or not.
     */
    fun curvatureDrive(
        linearPercent: Double,
        curvaturePercent: Double,
        isQuickTurn: Boolean,
    ) {
        val (l, r) = driveHelper.curvatureDrive(linearPercent, curvaturePercent, isQuickTurn)
        setPercent(l, r)
    }

    /**
     * Set a percent output to the drive motors.
     * @param left The left percent.
     * @param right The right percent.
     */
    fun setPercent(left: Double, right: Double) {
        periodicIO.desiredOutput = Output.Percent(left, right)
        periodicIO.leftFeedforward = 0.volts
        periodicIO.rightFeedforward = 0.volts
    }

    /**
     * Set a percent output to the drive motors.
     * @param pair The left and right velocities.
     */
    fun setPercent(pair: Pair<Double, Double>) {
        periodicIO.desiredOutput = Output.Percent(pair.first, pair.second)
        periodicIO.leftFeedforward = 0.volts
        periodicIO.rightFeedforward = 0.volts
    }

    /**
     * Resets the position on the field.
     * @param pose The position to reset to.
     */
    fun resetPosition(pose: Pose2d) {
        odometry.resetPosition(
            gyro(),
            leftMotor.encoder.position.value,
            rightMotor.encoder.position.value,
            pose,
        )
    }

    fun followTrajectory(trajectory: Trajectory, mirrored: Boolean = false) =
        WestCoastTrajectoryTrackerCommand(this, Source(if (mirrored) trajectory.mirror() else trajectory))

    fun followTrajectory(trajectory: Trajectory, mirrored: BooleanSource) =
        WestCoastTrajectoryTrackerCommand(this, mirrored.map(trajectory.mirror(), trajectory))

    fun followTrajectory(trajectory: Source<Trajectory>) =
        WestCoastTrajectoryTrackerCommand(this, trajectory)

    fun characterize() = CharacterizationCommand(this)

    /**
     * Represents periodic data
     */
    protected class PeriodicIO {
        var leftVoltage: SIUnit<Volt> = 0.volts
        var rightVoltage: SIUnit<Volt> = 0.volts

        var leftCurrent: SIUnit<Ampere> = 0.amps
        var rightCurrent: SIUnit<Ampere> = 0.amps

        var leftPosition: SIUnit<Meter> = 0.meters
        var rightPosition: SIUnit<Meter> = 0.meters

        var leftVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var rightVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds

        var gyro: Rotation2d = Rotation2d()

        var desiredOutput: Output = Output.Nothing
        var leftFeedforward: SIUnit<Volt> = 0.volts
        var rightFeedforward: SIUnit<Volt> = 0.volts
    }

    /**
     * Represents the typical outputs for the drivetrain.
     */
    protected sealed class Output {
        // No outputs
        object Nothing : Output()

        // Percent Output
        class Percent(val left: Double, val right: Double) : Output()

        // Velocity Output
        class Velocity(
            val left: SIUnit<LinearVelocity>,
            val right: SIUnit<LinearVelocity>,
        ) : Output()
    }
}
