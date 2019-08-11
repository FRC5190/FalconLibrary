/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.IMotorController
import com.ctre.phoenix.motorcontrol.NeutralMode
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.*
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100ms
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100msPerSecond
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.unitlessValue
import org.ghrobotics.lib.motors.AbstractFalconMotor
import org.ghrobotics.lib.motors.FalconMotor
import kotlin.math.roundToInt
import kotlin.properties.Delegates

abstract class FalconCTRE<K : SIKey>(
    val motorController: IMotorController,
    val model: NativeUnitModel<K>
) : AbstractFalconMotor<K>() {

    private var lastDemand =
        Demand(ControlMode.Disabled, 0.0, DemandType.Neutral, 0.0)

    override val encoder = FalconCTREEncoder(motorController, 0, model)

    override val voltageOutput: SIUnit<Volt>
        get() = motorController.motorOutputVoltage.volts

    override var outputInverted: Boolean by Delegates.observable(false) { _, _, newValue ->
        motorController.inverted = newValue
    }

    override var brakeMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        motorController.setNeutralMode(if (newValue) NeutralMode.Brake else NeutralMode.Coast)
    }

    override var voltageCompSaturation: SIUnit<Volt> by Delegates.observable(12.0.volts) { _, _, newValue ->
        motorController.configVoltageCompSaturation(newValue.value, 0)
        motorController.enableVoltageCompensation(true)
    }

    override var motionProfileCruiseVelocity: SIUnit<Velocity<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        motorController.configMotionCruiseVelocity(
            model.toNativeUnitVelocity(newValue).nativeUnitsPer100ms.roundToInt(),
            0
        )
    }
    override var motionProfileAcceleration: SIUnit<Acceleration<K>> by Delegates.observable(SIUnit(0.0)) { _, _, newValue ->
        motorController.configMotionAcceleration(
            model.toNativeUnitAcceleration(newValue).nativeUnitsPer100msPerSecond.roundToInt(),
            0
        )
    }

    init {
        motorController.configVoltageCompSaturation(12.0, 0)
        motorController.enableVoltageCompensation(true)
    }

    override fun setVoltage(voltage: SIUnit<Volt>, arbitraryFeedForward: SIUnit<Volt>) =
        sendDemand(
            Demand(
                ControlMode.PercentOutput, (voltage / voltageCompSaturation).unitlessValue,
                DemandType.ArbitraryFeedForward, (arbitraryFeedForward / voltageCompSaturation).unitlessValue
            )
        )

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: SIUnit<Volt>) =
        sendDemand(
            Demand(
                ControlMode.PercentOutput, dutyCycle,
                DemandType.ArbitraryFeedForward, (arbitraryFeedForward / voltageCompSaturation).unitlessValue
            )
        )

    override fun setVelocity(velocity: SIUnit<Velocity<K>>, arbitraryFeedForward: SIUnit<Volt>) =
        sendDemand(
            Demand(
                ControlMode.Velocity, model.toNativeUnitVelocity(velocity).nativeUnitsPer100ms,
                DemandType.ArbitraryFeedForward, (arbitraryFeedForward / voltageCompSaturation).unitlessValue
            )
        )

    override fun setPosition(position: SIUnit<K>, arbitraryFeedForward: SIUnit<Volt>) =
        sendDemand(
            Demand(
                if (useMotionProfileForPosition) ControlMode.MotionMagic else ControlMode.Position,
                model.toNativeUnitPosition(position).value,
                DemandType.ArbitraryFeedForward, (arbitraryFeedForward / voltageCompSaturation).unitlessValue
            )
        )

    override fun setNeutral() = sendDemand(
        Demand(
            ControlMode.Disabled,
            0.0,
            DemandType.Neutral,
            0.0
        )
    )

    fun sendDemand(demand: Demand) {
        if (demand != lastDemand) {
            motorController.set(demand.mode, demand.demand0, demand.demand1Type, demand.demand1)
            lastDemand = demand
        }
    }

    override fun follow(motor: FalconMotor<*>): Boolean =
        if (motor is FalconCTRE<*>) {
            motorController.follow(motor.motorController)
            true
        } else {
            super.follow(motor)
        }

    data class Demand(
        val mode: ControlMode,
        val demand0: Double,
        val demand1Type: DemandType,
        val demand1: Double
    )

}