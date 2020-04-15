/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.pwf

import com.playingwithfusion.CANVenom
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.RobotController
import kotlin.properties.Delegates
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.motors.AbstractFalconMotor
import org.ghrobotics.lib.motors.FalconMotor

internal fun getVenomID(venom: CANVenom): Int {
    val field = venom.javaClass.getDeclaredField("m_motorID")
    field.isAccessible = true
    return field.getInt(venom)
}

/**
 * Wrapper around the Venom motor controller.
 *
 * @param venom The underlying motor controller.
 * @param model The native unit model.
 */
class FalconVenom<K : SIKey>(
    @Suppress("MemberVisibilityCanBePrivate") val venom: CANVenom,
    private val model: NativeUnitModel<K>,
    units: K
) : AbstractFalconMotor<K>("FalconVenom[${getVenomID(venom)}]") {

    /**
     * Alternate constructor where users can supply ID and native unit model.
     *
     * @param id The ID of the motor controller.
     * @param model The native unit model.
     */
    constructor(id: Int, model: NativeUnitModel<K>, units: K) : this(CANVenom(id), model, units)

    /**
     * The encoder for the Spark MAX.
     */
    override val encoder = FalconVenomEncoder(venom, model, units)

    /**
     * Returns the voltage across the motor windings.
     */
    override val voltageOutput: SIUnit<Volt>
        get() = venom.outputVoltage.volts

    /**
     * Returns the current drawn by the motor.
     */
    override val drawnCurrent: SIUnit<Ampere>
        get() = venom.outputCurrent.amps

    /**
     * Whether the output of the motor is inverted or not..
     */
    override var outputInverted by Delegates.observable(false) { _, _, newValue ->
        venom.inverted = newValue
    }

    /**
     * Configures brake mode for the motor controller.
     */
    override var brakeMode by Delegates.observable(false) { _, _, newValue ->
        venom.brakeCoastMode = if (newValue) CANVenom.BrakeCoastMode.Brake else CANVenom.BrakeCoastMode.Coast
    }

    /**
     * Configures voltage compensation for the motor controller.
     */
    override var voltageCompSaturation by Delegates.observable(12.volts) { _, _, _ ->
        DriverStation.reportError("Voltage Compensation is not supported on the Venom", false)
    }

    /**
     * Configures the motion profile cruise velocity.
     */
    override var motionProfileCruiseVelocity: SIUnit<Velocity<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        venom.maxSpeed = model.toNativeUnitVelocity(newValue).value * 60.0
    }

    /**
     * Configures the max acceleration for the motion profile generated by Smart Motion.
     */
    override var motionProfileAcceleration: SIUnit<Acceleration<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        venom.maxAcceleration = model.toNativeUnitAcceleration(newValue).value * 60.0
    }

    override var softLimitForward: SIUnit<K> by Delegates.observable(SIUnit(0.0)) { _, _, _ ->
        DriverStation.reportError("Soft Limits are not supported on the Venom.", false)
    }

    override var softLimitReverse: SIUnit<K> by Delegates.observable(SIUnit(0.0)) { _, _, _ ->
        DriverStation.reportError("Soft Limits are not supported on the Venom.", false)
    }

    /**
     * Sets a certain voltage across the motor windings.
     *
     * @param voltage The voltage to set.
     * @param arbitraryFeedForward The arbitrary feedforward to add to the motor output.
     */
    override fun setVoltage(voltage: SIUnit<Volt>, arbitraryFeedForward: SIUnit<Volt>) {
        if (simVoltageOutput != null) {
            simVoltageOutput.set(voltage.value + arbitraryFeedForward.value)
            return
        }

        venom.setCommand(CANVenom.ControlMode.VoltageControl, voltage.value, 0.0, arbitraryFeedForward.value / 6.0)
    }

    /**
     * Commands a certain duty cycle to the motor.
     *
     * @param dutyCycle The duty cycle to command.
     * @param arbitraryFeedForward The arbitrary feedforward to add to the motor output.
     */
    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: SIUnit<Volt>) {
        if (simVoltageOutput != null) {
            simVoltageOutput.set(dutyCycle * RobotController.getBatteryVoltage() + arbitraryFeedForward.value)
            return
        }
        venom.setCommand(CANVenom.ControlMode.Proportional, dutyCycle, 0.0, arbitraryFeedForward.value / 6.0)
    }

    /**
     * Sets the velocity setpoint of the motor controller.
     *
     * @param velocity The velocity setpoint.
     * @param arbitraryFeedForward The arbitrary feedforward to add to the motor output.
     */
    override fun setVelocity(velocity: SIUnit<Velocity<K>>, arbitraryFeedForward: SIUnit<Volt>) {
        venom.setCommand(
            CANVenom.ControlMode.SpeedControl, model.toNativeUnitVelocity(velocity).value * 60.0,
            0.0, arbitraryFeedForward.value / 6.0
        )
    }

    /**
     * Sets the position setpoint of the motor controller. This uses a motion profile
     * if motion profiling is configured.
     *
     * @param position The position setpoint.
     * @param arbitraryFeedForward The arbitrary feedforward to add to the motor output.
     */
    override fun setPosition(position: SIUnit<K>, arbitraryFeedForward: SIUnit<Volt>) {
        venom.setCommand(
            CANVenom.ControlMode.PositionControl, model.toNativeUnitPosition(position).value,
            0.0, arbitraryFeedForward.value / 6.0
        )
    }

    /**
     * Gives the motor neutral output.
     */
    override fun setNeutral() {
        setDutyCycle(0.0)
    }

    /**
     * Follows the output of another motor controller.
     *
     * @param motor The other motor controller.
     */
    override fun follow(motor: FalconMotor<*>): Boolean = if (motor is FalconVenom<*>) {
        venom.follow(motor.venom)
        true
    } else {
        super.follow(motor)
    }
}

fun <K : SIKey> falconVenom(
    venom: CANVenom,
    model: NativeUnitModel<K>,
    units: K,
    block: FalconVenom<K>.() -> Unit
) = FalconVenom(venom, model, units).also(block)

fun <K : SIKey> falconVenom(
    id: Int,
    model: NativeUnitModel<K>,
    units: K,
    block: FalconVenom<K>.() -> Unit
) = FalconVenom(id, model, units).also(block)
