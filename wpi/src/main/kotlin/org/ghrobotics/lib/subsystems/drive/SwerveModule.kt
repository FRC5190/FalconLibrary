package org.ghrobotics.lib.subsystems.drive


import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.motors.FalconMotor

/**
 * Falcon swerve module
 *
 * @property driveMotor
 * @property turn
 * @constructor Create empty Falcon swerve module
 */
class FalconSwerveModule(private val driveMotor: FalconMotor<Meter>, private val turnMotor: FalconMotor<Radian>) {
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
    fun setVelocity(velocity: SIUnit<LinearVelocity>, arbitraryFeedForward: SIUnit<Volt> = SIUnit(0.0)) { driveMotor.setVelocity(velocity, arbitraryFeedForward)}

    /**
     * Set angle of turn motors
     *
     * @param angle
     */
    fun setAngle(angle: SIUnit<Radian>) { turnMotor.setPosition(angle) }

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

    val voltageOutput get() = driveMotor.voltageOutput

    val velocity get() = driveMotor.encoder.velocity

    val drawnCurrent get() = driveMotor.drawnCurrent

    val drivePosition get() = driveMotor.encoder.position

    val driveVelocity get() = driveMotor.encoder.velocity

    val anglePosition get() = turnMotor.encoder.position





}