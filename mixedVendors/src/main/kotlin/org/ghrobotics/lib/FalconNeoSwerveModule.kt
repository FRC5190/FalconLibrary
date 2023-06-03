/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib

import com.revrobotics.CANSparkMaxLowLevel
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.inDegrees
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.motors.AbstractFalconAbsoluteEncoder
import org.ghrobotics.lib.motors.ctre.FalconCanCoder
import org.ghrobotics.lib.motors.rev.FalconMAX
import org.ghrobotics.lib.motors.rev.falconMAX
import org.ghrobotics.lib.subsystems.drive.swerve.AbstractFalconSwerveModule
import org.ghrobotics.lib.subsystems.drive.swerve.SwerveModuleConstants
import kotlin.math.PI
import kotlin.math.abs

class FalconNeoSwerveModule(private val swerveModuleConstants: SwerveModuleConstants) :
    AbstractFalconSwerveModule<FalconMAX<Meter>, FalconMAX<Radian>>, Sendable {
    private var resetIteration: Int = 500
    private var referenceAngle: Double = 0.0
    val name = swerveModuleConstants.kName
    private val maxVoltage = swerveModuleConstants.kDriveMaxVoltage

    override var encoder: AbstractFalconAbsoluteEncoder<Radian> = FalconCanCoder(
        swerveModuleConstants.kCanCoderId,
        swerveModuleConstants.kCanCoderNativeUnitModel,
        swerveModuleConstants.kAzimuthEncoderHomeOffset,
    )

    override var driveMotor = with(swerveModuleConstants) {
        falconMAX(
            kDriveTalonId,
            CANSparkMaxLowLevel.MotorType.kBrushless,
            kDriveNativeUnitModel,
        ) {
            outputInverted = kInvertDrive
            brakeMode = kDriveBrakeMode
            voltageCompSaturation = 12.volts
            smartCurrentLimit = 40.amps
            canSparkMax.run {
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100)
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20)
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 20)
            }
        }
    }
    override var azimuthMotor = with(swerveModuleConstants) {
        falconMAX(
            kAzimuthTalonId,
            CANSparkMaxLowLevel.MotorType.kBrushless,
            kAzimuthNativeUnitModel,
        ) {
            outputInverted = kInvertAzimuth
            brakeMode = kAzimuthBrakeMode
            canSparkMax.run {
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 100)
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus1, 20)
                setPeriodicFramePeriod(CANSparkMaxLowLevel.PeriodicFrame.kStatus0, 20)
            }
            voltageCompSaturation = 12.volts
            smartCurrentLimit = 20.amps
            controller.run {
                ff = kAzimuthKf
                p = kAzimuthKp
                i = kAzimuthKi
                d = kAzimuthKd
                iZone = kAzimuthIZone
                setFeedbackDevice(canSparkMax.encoder)
            }
        }
    }

    override fun setState(state: SwerveModuleState, arbitraryFeedForward: SIUnit<Volt>) {
        var setAngle = state.angle.radians % (2 * Math.PI)
        var voltage = (state.speedMetersPerSecond / swerveModuleConstants.kDriveMaxSpeed) * maxVoltage
        if (setAngle < 0.0) setAngle += 2.0 * Math.PI

        var diff = setAngle - stateAngle()
        if (diff >= PI) {
            setAngle -= 2.0 * PI
        } else if (diff < -PI) {
            setAngle += 2.0 * PI
        }
        diff = setAngle - stateAngle()
        if (diff > PI / 2.0 || diff < -PI / 2.0) {
            setAngle += PI
            voltage *= -1
        }

        setAngle %= 2.0 * PI
        if (setAngle < 0.0) setAngle += 2.0 * PI

        setVoltage(voltage)
        setAngle(setAngle)
    }

    private fun stateAngle(): Double {
        var motorAngle = azimuthMotor.encoder.position.value
        motorAngle %= 2.0 * PI
        if (motorAngle < 0.0) motorAngle += 2.0 * PI
        return motorAngle
    }

    // Keep an eye on this function
    override fun swervePosition(): SwerveModulePosition = SwerveModulePosition(
        drivePosition.value,
        edu.wpi.first.math.geometry.Rotation2d(stateAngle()),
    )

    override fun setNeutral() {
        driveMotor.setNeutral()
    }

    override fun setAngle(angle: Double) {
        var currentAngleRadians = azimuthMotor.encoder.position.value

        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        // Reset the NEO's encoder periodically when the module is not rotating.
        // Sometimes (~5% of the time) when we initialize, the absolute encoder isn't fully set up, and we don't
        // end up getting a good reading. If we reset periodically this won't matter anymore.
        if (abs(azimuthMotor.encoder.velocity.value) < ENCODER_RESET_MAX_ANGULAR_VELOCITY) {
            if (++resetIteration >= ENCODER_RESET_ITERATIONS) {
                resetIteration = 0
                val absoluteAngle: SIUnit<Radian> = encoder.absolutePosition
                azimuthMotor.encoder.resetPosition(absoluteAngle)
                currentAngleRadians = absoluteAngle.value
            }
        } else {
            resetIteration++
        }

        var currentAngleRadiansMod = currentAngleRadians % (2.0 * Math.PI)
        if (currentAngleRadiansMod < 0.0) {
            currentAngleRadiansMod += 2.0 * Math.PI
        }

        // The reference angle has the range [0, 2pi) but the Neo's encoder can go above that

        // The reference angle has the range [0, 2pi) but the Neo's encoder can go above that
        var adjustedReferenceAngleRadians: Double = angle + currentAngleRadians - currentAngleRadiansMod
        if (angle - currentAngleRadiansMod > Math.PI) {
            adjustedReferenceAngleRadians -= 2.0 * Math.PI
        } else if (angle - currentAngleRadiansMod < -Math.PI) {
            adjustedReferenceAngleRadians += 2.0 * Math.PI
        }

        referenceAngle = angle

        azimuthMotor.setPosition(adjustedReferenceAngleRadians.radians)
    }

    override fun setVoltage(voltage: Double) {
        driveMotor.setVoltage(voltage.volts)
    }

    fun setPositionToAbsoluteEncoder() {
        azimuthMotor.encoder.resetPosition(encoder.absolutePosition)
    }

    override val voltageOutput: SIUnit<Volt> get() = driveMotor.voltageOutput
    override val drawnCurrent: SIUnit<Ampere> get() = driveMotor.drawnCurrent
    override val drivePosition: SIUnit<Meter> get() = driveMotor.encoder.position
    override val driveVelocity: SIUnit<Velocity<Meter>> get() = driveMotor.encoder.velocity
    override val anglePosition: SIUnit<Radian> get() = encoder.position

    companion object {
        private const val ENCODER_RESET_ITERATIONS = 500
        private val ENCODER_RESET_MAX_ANGULAR_VELOCITY = Math.toRadians(0.5)
    }

    override fun initSendable(builder: SendableBuilder?) {
        builder!!.run {
            addDoubleProperty("Absolute Position", {
                encoder.absolutePosition.inDegrees()
            }, {})
            addDoubleProperty("Drive Voltage", {
                driveMotor.voltageOutput.value
            }, {})
            addDoubleProperty("State Angle", { stateAngle() }, {})
        }
    }
}
