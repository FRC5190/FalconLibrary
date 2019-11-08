/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveOdometry
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj.trajectory.Trajectory
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.twodim.trajectory.mirror
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.inFeet
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.motors.FalconMotor
import org.ghrobotics.lib.physics.MotorCharacterization
import org.ghrobotics.lib.subsystems.EmergencyHandleable
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map

/**
 * Represents a typical west coast drive that is built by Team 5190.
 */
abstract class FalconWestCoastDrivetrain : TrajectoryTrackerDriveBase(), EmergencyHandleable {
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
    abstract val leftCharacterization: MotorCharacterization<Meter>

    /**
     * The characterization for the right gearbox.
     */
    abstract val rightCharacterization: MotorCharacterization<Meter>

    /**
     * The rotation source / gyro
     */
    abstract val gyro: Source<Rotation2d>

    /**
     * Get the robot's position on the field.
     */
    override var robotPosition: Pose2d = Pose2d()

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
            periodicIO.gyro, DifferentialDriveWheelSpeeds(
                periodicIO.leftVelocity.value, periodicIO.rightVelocity.value
            )
        )

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

        LiveDashboard.robotHeading = robotPosition.rotation.radians
        LiveDashboard.robotX = robotPosition.translation.x_u.inFeet()
        LiveDashboard.robotY = robotPosition.translation.y_u.inFeet()
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
    override fun setOutput(
        leftVelocity: SIUnit<LinearVelocity>,
        rightVelocity: SIUnit<LinearVelocity>,
        leftAcceleration: SIUnit<LinearAcceleration>,
        rightAcceleration: SIUnit<LinearAcceleration>
    ) {
        periodicIO.leftFeedforward = leftCharacterization.getVoltage(
            leftVelocity, leftAcceleration
        )
        periodicIO.rightFeedforward = rightCharacterization.getVoltage(
            rightVelocity, rightAcceleration
        )
        periodicIO.desiredOutput = Output.Velocity(
            leftVelocity, rightVelocity
        )
    }

    /**
     * Drives the robot using arcade drive.
     *
     * @param linearPercent The percent for linear motion.
     * @param rotationPercent The percent for angular rotation.
     */
    fun arcadeDrive(
        linearPercent: Double,
        rotationPercent: Double
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
        isQuickTurn: Boolean
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

    fun followTrajectory(trajectory: Trajectory, mirrored: Boolean = false) =
        TrajectoryTrackerCommand(this, Source(if (mirrored) trajectory.mirror() else trajectory))

    fun followTrajectory(trajectory: Trajectory, mirrored: BooleanSource) =
        TrajectoryTrackerCommand(this, mirrored.map(trajectory.mirror(), trajectory))

    fun followTrajectory(trajectory: Source<Trajectory>) =
        TrajectoryTrackerCommand(this, trajectory)

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
            val right: SIUnit<LinearVelocity>
        ) : Output()
    }
}
