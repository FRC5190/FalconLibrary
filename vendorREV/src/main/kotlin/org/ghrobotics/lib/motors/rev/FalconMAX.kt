/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.rev

import com.revrobotics.AlternateEncoderType
import com.revrobotics.CANPIDController
import com.revrobotics.CANSparkMax
import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
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

/**
 * Creates a Spark MAX motor controller. The alternate encoder CPR is defaulted
 * to the CPR of the REV Through Bore Encoder.
 */
class FalconMAX<K : SIKey>(
    val canSparkMax: CANSparkMax,
    val model: NativeUnitModel<K>,
    useAlternateEncoder: Boolean = false,
    alternateEncoderCPR: Int = 8192
) : AbstractFalconMotor<K>() {

    /**
     * Creates a Spark MAX motor controller. The alternate encoder CPR is defaulted
     * to the CPR of the REV Through Bore Encoder.
     */
    constructor(
        id: Int,
        type: CANSparkMaxLowLevel.MotorType,
        model: NativeUnitModel<K>,
        useAlternateEncoder: Boolean = false,
        alternateEncoderCPR: Int = 8196
    ) : this(
        CANSparkMax(id, type), model, useAlternateEncoder, alternateEncoderCPR
    )

    val controller: CANPIDController = canSparkMax.pidController
    override val encoder = FalconMAXEncoder(
        if (useAlternateEncoder) canSparkMax.getAlternateEncoder(
            AlternateEncoderType.kQuadrature,
            alternateEncoderCPR
        ) else canSparkMax.encoder, model
    )

    init {
        controller.setFeedbackDevice(encoder.canEncoder)
    }

    override val voltageOutput: SIUnit<Volt>
        get() = (canSparkMax.appliedOutput * canSparkMax.busVoltage).volts

    override val drawnCurrent: SIUnit<Ampere>
        get() = canSparkMax.outputCurrent.amps

    override var outputInverted: Boolean by Delegates.observable(false) { _, _, newValue ->
        canSparkMax.inverted = newValue
    }

    override var brakeMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        canSparkMax.idleMode = if (newValue) CANSparkMax.IdleMode.kBrake else CANSparkMax.IdleMode.kCoast
    }

    override var voltageCompSaturation: SIUnit<Volt> by Delegates.observable(12.0.volts) { _, _, newValue ->
        canSparkMax.enableVoltageCompensation(newValue.value)
    }

    override var motionProfileCruiseVelocity: SIUnit<Velocity<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        controller.setSmartMotionMaxVelocity(model.toNativeUnitVelocity(newValue).value * 60.0, 0)
    }
    override var motionProfileAcceleration: SIUnit<Acceleration<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        controller.setSmartMotionMaxAccel(model.toNativeUnitAcceleration(newValue).value * 60.0, 0)
    }

    init {
        canSparkMax.enableVoltageCompensation(12.0)
    }

    override fun setVoltage(voltage: SIUnit<Volt>, arbitraryFeedForward: SIUnit<Volt>) {
        controller.setReference(voltage.value, ControlType.kVoltage, 0, arbitraryFeedForward.value)
    }

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: SIUnit<Volt>) {
        controller.setReference(dutyCycle, ControlType.kDutyCycle, 0, arbitraryFeedForward.value)
    }

    override fun setVelocity(velocity: SIUnit<Velocity<K>>, arbitraryFeedForward: SIUnit<Volt>) {
        controller.setReference(
            model.toNativeUnitVelocity(velocity).value * 60,
            ControlType.kVelocity, 0, arbitraryFeedForward.value
        )
    }

    override fun setPosition(position: SIUnit<K>, arbitraryFeedForward: SIUnit<Volt>) {
        controller.setReference(
            model.toNativeUnitPosition(position).value,
            if (useMotionProfileForPosition) ControlType.kSmartMotion else ControlType.kPosition,
            0, arbitraryFeedForward.value
        )
    }

    override fun follow(motor: FalconMotor<*>): Boolean =
        if (motor is FalconMAX<*>) {
            canSparkMax.follow(motor.canSparkMax)
            true
        } else {
            super.follow(motor)
        }

    override fun setNeutral() = setDutyCycle(0.0)
}

fun <K : SIKey> falconMAX(
    canSparkMax: CANSparkMax,
    model: NativeUnitModel<K>,
    useAlternateEncoder: Boolean = false,
    alternateEncoderCPR: Int = 8192,
    block: FalconMAX<K>.() -> Unit
) = FalconMAX(canSparkMax, model, useAlternateEncoder, alternateEncoderCPR).also(block)

fun <K : SIKey> falconMAX(
    id: Int,
    type: CANSparkMaxLowLevel.MotorType,
    model: NativeUnitModel<K>,
    useAlternateEncoder: Boolean = false,
    alternateEncoderCPR: Int = 8192,
    block: FalconMAX<K>.() -> Unit
) = FalconMAX(id, type, model, useAlternateEncoder, alternateEncoderCPR).also(block)
