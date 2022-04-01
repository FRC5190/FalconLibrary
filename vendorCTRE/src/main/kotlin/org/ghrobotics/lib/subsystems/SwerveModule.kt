/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems

import edu.wpi.first.math.kinematics.SwerveModuleState
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
import org.ghrobotics.lib.motors.ctre.falconFX

/**
 * Falcon swerve module
 *
 * @property driveMotor
 * @property turn
 * @constructor Create empty Falcon swerve module
 */
class FalconSwerveModule(val swerveModuleConstants: SwerveModuleConstants) : AbstractFalconSwerveModule {
    override var driveMotor: FalconMotor<Meter> = with(swerveModuleConstants) {
        falconFX(kDriveTalonId, kDriveNativeUnitModel) {
            brakeMode = kDriveBrakeMode
            outputInverted = kInvertDrive
            voltageCompSaturation = kDriveMaxVoltage.volts
        }
    }

    override var turnMotor: FalconMotor<Radian> = with(swerveModuleConstants) {
        falconFX(kAzimuthTalonId, kAzimuthNativeUnitModel) {
            outputInverted = kInvertAzimuth
            brakeMode = kAzimuthBrakeMode
            voltageCompSaturation = kAzimuthMaxVoltage.volts
            motionProfileAcceleration = kAzimuthAcceleration
            motionProfileCruiseVelocity = kAzimuthCruiseVelocity

            motorController.run {
                config_kP(0, kAzimuthKp, 30)
                config_kI(0, kAzimuthKi, 30)
                config_kD(0, kAzimuthKd, 30)
                config_kF(0, kAzimuthKf, 30)
                config_IntegralZone(0, kAzimuthIZone.toDouble(), 30)
                configAllowableClosedloopError(0, kAzimuthClosedLoopAllowableError.toDouble(), 30)
            }
        }
    }

    class SwerveModuleConstants {
        var kName = "Name"
        var kDriveTalonId = -1
        var kAzimuthTalonId = -1

        // general azimuth
        var kInvertAzimuth = false
        var kInvertAzimuthSensorPhase = false
        var kAzimuthBrakeMode = true // neutral mode could change
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
        var kInvertDrive = true
        var kInvertDriveSensorPhase = false
        var kDriveBrakeMode = true // neutral mode could change
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
    override fun setNeutral() { driveMotor.setNeutral() }

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

    override fun setControls(speed: Double, azimuth: Rotation2d) {
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

    override fun setState(state: SwerveModuleState, arbitraryFeedForward: SIUnit<Volt>) {
        setVelocity(SIUnit(state.speedMetersPerSecond), arbitraryFeedForward)
        setAngle(SIUnit(state.angle.radians))
    }

    /**
     * Resets turnMotor encoders
     *
     * @param angle
     */
    override fun resetAngle(angle: SIUnit<Radian>) { turnMotor.encoder.resetPosition(angle) }

    /**
     * Reset drive encoders
     *
     * @param position
     */
    override fun resetDriveEncoder(position: SIUnit<Meter>) { driveMotor.encoder.resetPosition(position) }

    /**
     * Resets encoders for drive and turn encoders
     *
     */
    override fun reset() {
        resetAngle()
        resetDriveEncoder()
    }

    override fun state(): SwerveModuleState {
        return SwerveModuleState(driveMotor.encoder.velocity.value, edu.wpi.first.math.geometry.Rotation2d(turnMotor.encoder.position.value))
    }

    override val voltageOutput get() = driveMotor.voltageOutput

    override val drawnCurrent get() = driveMotor.drawnCurrent

    override val drivePosition get() = driveMotor.encoder.position

    override val driveVelocity get() = driveMotor.encoder.velocity

    override val anglePosition get() = turnMotor.encoder.position
}
