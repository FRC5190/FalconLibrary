/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.kinematics.SwerveModuleState
import kotlin.math.sign
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.motors.FalconMotor

/**
 * Falcon swerve module
 *
 * @property driveMotor
 * @property turn
 * @constructor Create empty Falcon swerve module
 */
class FalconSwerveModule() {
    private lateinit var driveMotor: FalconMotor<Meter>
    private lateinit var turnMotor: FalconMotor<Radian>

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
        if (Math.abs(raw_error) > Math.PI) {
            raw_error -= Math.PI * 2 * Math.signum(raw_error)
        }

        // error is -180 to 180
        // is wheel reversible logic

        // error is -180 to 180
        // is wheel reversible logic
        if (Math.abs(raw_error) > Math.PI / 2) {
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
