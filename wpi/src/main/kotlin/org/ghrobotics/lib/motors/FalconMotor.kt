/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts

typealias LinearFalconMotor = FalconMotor<Meter>
typealias AngularFalconMotor = FalconMotor<Radian>

interface FalconMotor<K : SIKey> {

    /**
     * The encoder attached to the motor
     */
    val encoder: FalconEncoder<K>

    /**
     * The voltage output of the motor controller in volts
     */
    val voltageOutput: SIUnit<Volt>

    /**
     * The current drawn by the motor
     */
    val drawnCurrent: SIUnit<Ampere>

    /**
     * Inverts the output given to the motor
     */
    var outputInverted: Boolean

    /**
     *  When enabled, motor leads are commonized electrically to reduce motion
     */
    var brakeMode: Boolean

    /**
     * Configures the max voltage output given to the motor
     */
    var voltageCompSaturation: SIUnit<Volt>

    /**
     *  Peak target velocity that the on board motion profile generator will use
     *  Unit is [K]/s
     */
    var motionProfileCruiseVelocity: SIUnit<Velocity<K>>

    /**
     *  Acceleration that the on board motion profile generator will
     *  Unit is [K]/s/s
     */
    var motionProfileAcceleration: SIUnit<Acceleration<K>>

    /**
     * Enables the use of on board motion profiling for position mode
     */
    var useMotionProfileForPosition: Boolean

    /**
     * Soft limit in the forward direction.
     */
    var softLimitForward: SIUnit<K>

    /**
     * Soft limit in the reverse direction.
     */
    var softLimitReverse: SIUnit<K>

    fun follow(motor: FalconMotor<*>): Boolean

    /**
     * Sets the output [voltage] in volts and [arbitraryFeedForward] in volts
     */
    fun setVoltage(voltage: SIUnit<Volt>, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    /**
     * Sets the output [dutyCycle] in percent and [arbitraryFeedForward] in volts
     */
    fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    /**
     * Sets the output [velocity] in [K]/s and [arbitraryFeedForward] in volts
     */
    fun setVelocity(velocity: SIUnit<Velocity<K>>, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    /**
     * Sets the output [position] in [K] and [arbitraryFeedForward] in volts
     */
    fun setPosition(position: SIUnit<K>, arbitraryFeedForward: SIUnit<Volt> = 0.0.volts)

    /**
     * Sets the output of the motor to neutral
     */
    fun setNeutral()
}
