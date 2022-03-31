/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.wpilibj.drive.RobotDriveBase
import kotlin.math.abs
import kotlin.math.sign
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.inches
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitLengthModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitRotationModel
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnits
import org.ghrobotics.lib.motors.FalconMotor

/**
 * Falcon swerve module
 *
 * @property driveMotor
 * @property turn
 * @constructor Create empty Falcon swerve module
 */
class FalconSwerveModule(val swerveModuleConstants: SwerveModuleConstants) {
    var driveMotor: FalconMotor<Meter> = with(swerveModuleConstants) {
        kDriveMotorBuilder(kDriveTalonId, kDriveNativeUnitModel).also {
            with(it) {
                brakeMode = kDriveBrakeMode
                outputInverted = kInvertDrive
                voltageCompSaturation = kDriveMaxVoltage.volts
            }
        }

    }

    var turnMotor: FalconMotor<Radian> = with(swerveModuleConstants) {
        kAzimuthMotorBuilder(kAzimuthTalonId, kAzimuthNativeUnitModel).also {
            with(it) {
                brakeMode = kAzimuthBrakeMode
                outputInverted = kInvertAzimuth
                voltageCompSaturation = kAzimuthMaxVoltage.volts
            }
        }
    }


    class SwerveModuleConstants {
        var kName = "Name"
        var kDriveTalonId = -1
        var kAzimuthTalonId = -1

        // general azimuth
        lateinit var kAzimuthMotorBuilder: (id: Int, unitModel: NativeUnitRotationModel) -> FalconMotor<Radian>
        var kInvertAzimuth = false
        var kInvertAzimuthSensorPhase = false
        var kAzimuthBrakeMode = true// neutral mode could change
//        var kAzimuthTicksPerRadian = 4096.0 / (2 * Math.PI) // for azimuth
        var kAzimuthNativeUnitModel = NativeUnitRotationModel(2048.nativeUnits)
        var kAzimuthEncoderHomeOffset = 0.0

        // azimuth motion
        var kAzimuthKp = 1.3
        var kAzimuthKi = 0.05
        var kAzimuthKd = 20.0
        var kAzimuthKf = 0.5421
        var kAzimuthIZone = 25
        var kAzimuthCruiseVelocity = SIUnit<Velocity<Radian>>(2.6) // 1698 native units
        var kAzimuthAcceleration = SIUnit<Acceleration<Radian>>(31.26) // 20379 Native Units | 12 * kAzimuthCruiseVelocity
        var kAzimuthClosedLoopAllowableError = 5

        // azimuth current/voltage
        var kAzimuthContinuousCurrentLimit = 30 // amps
        var kAzimuthPeakCurrentLimit = 60 // amps
        var kAzimuthPeakCurrentDuration = 200 // ms
        var kAzimuthEnableCurrentLimit = true
        var kAzimuthMaxVoltage = 10.0 // volts
        var kAzimuthVoltageMeasurementFilter = 8 // # of samples in rolling average

        // azimuth measurement
        var kAzimuthStatusFrame2UpdateRate = 10 // feedback for selected sensor, ms
        var kAzimuthStatusFrame10UpdateRate = 10 // motion magic, ms// dt for velocity measurements, ms
        var kAzimuthVelocityMeasurementWindow = 64 // # of samples in rolling average

        // general drive
        lateinit var kDriveMotorBuilder: (id: Int, unitModel: NativeUnitLengthModel) -> FalconMotor<Meter>
        var kInvertDrive = true
        var kInvertDriveSensorPhase = false
        var kDriveBrakeMode = true// neutral mode could change
        var kWheelDiameter = 4.0 // Probably should tune for each individual wheel maybe
        var kDriveNativeUnitModel = NativeUnitLengthModel(4096.nativeUnits, kWheelDiameter.inches)
        var kDriveDeadband = 0.01

        // drive current/voltage
        var kDriveContinuousCurrentLimit = 30 // amps
        var kDrivePeakCurrentLimit = 50 // amps
        var kDrivePeakCurrentDuration = 200 // ms
        var kDriveEnableCurrentLimit = true
        var kDriveMaxVoltage = 10.0 // volts
        var kDriveVoltageMeasurementFilter = 8 // # of samples in rolling average

        // drive measurement
        var kDriveStatusFrame2UpdateRate = 15 // feedback for selected sensor, ms
        var kDriveStatusFrame10UpdateRate = 200 // motion magic, ms// dt for velocity measurements, ms
        var kDriveVelocityMeasurementWindow = 64 // # of samples in rolling average
    }

    /**
     * Set percent output of drive motors
     *
     * @param percent
     * @param arbitraryFeedForward
     */
    fun setPercent(percent: Double, arbitraryFeedForward: SIUnit<Volt> = SIUnit(0.0)) { driveMotor.setDutyCycle(percent, arbitraryFeedForward) }

    /**
     * Set drive motors of drive motors
     *
     */
    fun setNeutral() { driveMotor.setNeutral() }

    /**
     * Set velocity of drive motor
     *
     * @param velocity
     * @param arbitraryFeedForward
     */
    fun setVelocity(velocity: SIUnit<LinearVelocity>, arbitraryFeedForward: SIUnit<Volt> = SIUnit(0.0)) { driveMotor.setVelocity(velocity, arbitraryFeedForward) }

    /**
     * Set angle of turn motors
     *
     * @param angle
     */
    fun setAngle(angle: SIUnit<Radian>) { turnMotor.setPosition(angle) }

    fun setControls(speed: Double, azimuth: Rotation2d) {
        val current: Rotation2d = Rotation2d.fromRadians(turnMotor.encoder.position.value)
        var speed = speed

        var raw_error = current.distance(azimuth)
        if (abs(raw_error) > Math.PI) {
            raw_error -= Math.PI * 2 * sign(raw_error)
        }

        // error is -180 to 180
        // is wheel reversible logic

        // error is -180 to 180
        // is wheel reversible logic
        if (abs(raw_error) > Math.PI / 2) {
            speed *= -1
            raw_error -= Math.PI * sign(raw_error)
        }

        val final_setpoint: Double = turnMotor.encoder.position.value + raw_error
        // double adjusted_speed = speed * Math.abs(Math.cos(raw_error));

        // double adjusted_speed = speed * Math.abs(Math.cos(raw_error));
        driveMotor.setDutyCycle(speed)
        turnMotor.setPosition(SIUnit(final_setpoint))
    }

    fun setState(state: SwerveModuleState, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts) {
        setVelocity(SIUnit(state.speedMetersPerSecond), arbitraryFeedForward)
        setAngle(SIUnit(state.angle.radians))
    }

    /**
     * Resets turnMotor encoders
     *
     * @param angle
     */
    fun resetAngle(angle: SIUnit<Radian> = SIUnit(0.0)) { turnMotor.encoder.resetPosition(angle) }

    /**
     * Reset drive encoders
     *
     * @param position
     */
    fun resetDriveEncoder(position: SIUnit<Meter> = SIUnit(0.0)) { driveMotor.encoder.resetPosition(position) }

    /**
     * Resets encoders for drive and turn encoders
     *
     */
    fun reset() {
        resetAngle()
        resetDriveEncoder()
    }

    fun state(): SwerveModuleState {
        return SwerveModuleState(driveMotor.encoder.velocity.value, edu.wpi.first.math.geometry.Rotation2d(turnMotor.encoder.position.value))
    }


    val voltageOutput get() = driveMotor.voltageOutput

    val velocity get() = driveMotor.encoder.velocity

    val drawnCurrent get() = driveMotor.drawnCurrent

    val drivePosition get() = driveMotor.encoder.position

    val driveVelocity get() = driveMotor.encoder.velocity

    val anglePosition get() = turnMotor.encoder.position
}
