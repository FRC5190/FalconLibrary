package org.ghrobotics.lib.motors.rev

import com.revrobotics.CANPIDController
import com.revrobotics.CANSparkMax
import com.revrobotics.ControlType
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.motors.AbstractFalconMotor
import kotlin.properties.Delegates

class FalconMAX<T : SIUnit<T>>(
    val canSparkMax: CANSparkMax,
    val model: NativeUnitModel<T>
) : AbstractFalconMotor<T>() {

    private val controller: CANPIDController = canSparkMax.pidController
    override val encoder = FalconMAXEncoder(canSparkMax.encoder, model)

    override val voltageOutput: Double
        get() = canSparkMax.appliedOutput * canSparkMax.busVoltage

    override var outputInverted: Boolean by Delegates.observable(false) { _, _, newValue ->
        canSparkMax.inverted = newValue
    }

    override var brakeMode: Boolean by Delegates.observable(false) { _, _, newValue ->
        canSparkMax.idleMode = if (newValue) CANSparkMax.IdleMode.kBrake else CANSparkMax.IdleMode.kCoast
    }

    override var voltageCompSaturation: Double by Delegates.observable(12.0) { _, _, newValue ->
        canSparkMax.enableVoltageCompensation(newValue)
    }

    override var motionProfileCruiseVelocity: Double by Delegates.observable(0.0) { _, _, newValue ->
        controller.setSmartMotionMaxVelocity(model.toNativeUnitVelocity(newValue) * 60.0, 0)
    }
    override var motionProfileAcceleration: Double by Delegates.observable(0.0) { _, _, newValue ->
        controller.setSmartMotionMaxAccel(model.toNativeUnitAcceleration(newValue) * 60.0, 0)
    }

    init {
        canSparkMax.enableVoltageCompensation(12.0)
    }

    override fun setVoltage(voltage: Double, arbitraryFeedForward: Double) {
        controller.setReference(voltage, ControlType.kVoltage, 0, arbitraryFeedForward)
    }

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: Double) {
        controller.setReference(dutyCycle, ControlType.kDutyCycle, 0, arbitraryFeedForward)
    }

    override fun setVelocity(velocity: Double, arbitraryFeedForward: Double) {
        controller.setReference(
            model.toNativeUnitVelocity(velocity) * 60,
            ControlType.kVelocity, 0, arbitraryFeedForward
        )
    }

    override fun setPosition(position: Double, arbitraryFeedForward: Double) {
        controller.setReference(
            model.toNativeUnitPosition(position),
            if (useMotionProfileForPosition) ControlType.kSmartMotion else ControlType.kPosition,
            0, arbitraryFeedForward
        )
    }

    override fun setNeutral() = setDutyCycle(0.0)

}