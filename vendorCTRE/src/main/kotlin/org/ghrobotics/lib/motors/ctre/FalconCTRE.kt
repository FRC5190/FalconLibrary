package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import com.ctre.phoenix.motorcontrol.IMotorController
import com.ctre.phoenix.motorcontrol.NeutralMode
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.motors.AbstractFalconMotor
import org.ghrobotics.lib.motors.FalconMotor
import kotlin.properties.Delegates

abstract class FalconCTRE<T : SIUnit<T>>(
    val motorController: IMotorController,
    val model: NativeUnitModel<T>
) : AbstractFalconMotor<T>() {

    private var lastDemand =
        Demand(ControlMode.Disabled, 0.0, DemandType.Neutral, 0.0)

    private var compVoltage = 12.0

    override val encoder = FalconCTREEncoder(motorController, 0, model)

    override val voltageOutput: Double
        get() = motorController.motorOutputVoltage

    override var outputInverted: Boolean by Delegates.observable(false) { _, _, newValue ->
        motorController.inverted = newValue
    }

    override var brakeMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        motorController.setNeutralMode(if (newValue) NeutralMode.Brake else NeutralMode.Coast)
    }

    override var voltageCompSaturation: Double by Delegates.observable(12.0) { _, _, newValue ->
        motorController.configVoltageCompSaturation(newValue, 0)
        motorController.enableVoltageCompensation(true)
    }

    override var motionProfileCruiseVelocity: Double by Delegates.observable(0.0) { _, _, newValue ->
        motorController.configMotionCruiseVelocity((model.toNativeUnitVelocity(newValue) / 10.0).toInt(), 0)
    }
    override var motionProfileAcceleration: Double by Delegates.observable(0.0) { _, _, newValue ->
        motorController.configMotionAcceleration((model.toNativeUnitAcceleration(newValue) / 10.0).toInt(), 0)
    }

    init {
        motorController.configVoltageCompSaturation(12.0, 0)
        motorController.enableVoltageCompensation(true)
    }

    override fun setVoltage(voltage: Double, arbitraryFeedForward: Double) =
        sendDemand(
            Demand(
                ControlMode.PercentOutput, voltage / compVoltage,
                DemandType.ArbitraryFeedForward, arbitraryFeedForward / compVoltage
            )
        )

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: Double) =
        sendDemand(
            Demand(
                ControlMode.PercentOutput, dutyCycle,
                DemandType.ArbitraryFeedForward, arbitraryFeedForward / compVoltage
            )
        )

    override fun setVelocity(velocity: Double, arbitraryFeedForward: Double) =
        sendDemand(
            Demand(
                ControlMode.Velocity, model.toNativeUnitVelocity(velocity) / 10.0,
                DemandType.ArbitraryFeedForward, arbitraryFeedForward / compVoltage
            )
        )

    override fun setPosition(position: Double, arbitraryFeedForward: Double) =
        sendDemand(
            Demand(
                if (useMotionProfileForPosition) ControlMode.MotionMagic else ControlMode.Position,
                model.toNativeUnitPosition(position),
                DemandType.ArbitraryFeedForward, arbitraryFeedForward / compVoltage
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