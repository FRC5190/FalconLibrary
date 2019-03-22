package org.ghrobotics.lib.wrappers.rev

import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.minute
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnits
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnitsPer100ms
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnitsPer100msPerSecond
import kotlin.properties.Delegates

class NativeFalconSparkMax(
        id: Int,
        type: CANSparkMaxLowLevel.MotorType = MotorType.kBrushless
) : AbstractFalconSparkMax<NativeUnit>(id, type) {
    override var allowedClosedLoopError by Delegates.observable(0.nativeUnits) { _, _, newValue ->
        pidController.setSmartMotionAllowedClosedLoopError(newValue.value, 0)
    }
    override var motionCruiseVelocity by Delegates.observable(0.nativeUnitsPer100ms) { _, _, newValue ->
        pidController.setSmartMotionMaxVelocity(newValue.value, 0)
    }
    override var motionAcceleration by Delegates.observable(0.nativeUnitsPer100msPerSecond) { _, _, newValue ->
        pidController.setSmartMotionMaxAccel(newValue.value, 0)
    }
    override var sensorPosition
        get() = canEncoder.position.nativeUnits
        set(value) {
            canEncoder.position = value.value
        }

    // Get sensor velocity in nativeUnits per second
    // Spark max native units are in motor revolutions, and velocity is in rpm
    // so divide by 60 to get revolutions per second
    override val sensorVelocity
        get() = canEncoder.velocity.nativeUnits.div(1.minute)

    override fun set(controlType: ControlType, length: NativeUnit) {
        pidController.setReference(length.value, controlType)
    }

    override fun set(controlType: ControlType, velocity: Velocity<NativeUnit>) {
        pidController.setReference(velocity.value, controlType)
    }

    override fun set(controlType: ControlType, velocity: Velocity<NativeUnit>, outputPercent: Double) {
        pidController.setReference(velocity.value, controlType, 0, outputPercent)
    }

    override fun set(controlType: ControlType, length: NativeUnit, outputPercent: Double) {
        pidController.setReference(length.value, controlType, 0, outputPercent)
    }
}