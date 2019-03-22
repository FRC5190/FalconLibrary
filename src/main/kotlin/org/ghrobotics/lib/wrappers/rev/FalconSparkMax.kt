package org.ghrobotics.lib.wrappers.rev

import com.revrobotics.CANSparkMaxLowLevel
import com.revrobotics.ControlType
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.minute
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnits
import kotlin.properties.Delegates

typealias LinearFalconSparkMax = FalconSparkMax<Length>
typealias AngularFalconSparkMax = FalconSparkMax<Rotation2d>

open class FalconSparkMax <T : SIUnit<T>>(
        id: Int,
        private val model: NativeUnitModel<T>,
        motorType: CANSparkMaxLowLevel.MotorType = CANSparkMaxLowLevel.MotorType.kBrushless
) : AbstractFalconSparkMax<T>(id, motorType) {
    override var allowedClosedLoopError by Delegates.observable(model.zero) { _, _, newValue ->
        pidController.setSmartMotionAllowedClosedLoopError(newValue.value, 0)
    }
    override var motionCruiseVelocity by Delegates.observable(model.zero.velocity) { _, _, newValue ->
        pidController.setSmartMotionMaxVelocity(newValue.value, 0)
    }
    override var motionAcceleration by Delegates.observable(model.zero.acceleration) { _, _, newValue ->
        pidController.setSmartMotionMaxAccel(newValue.value, 0)
    }

    override var sensorPosition: T
        get() = model.fromNativeUnitPosition(canEncoder.position.nativeUnits)
        set(newValue) {
            canEncoder.position = model.toNativeUnitPosition(newValue.value)
        }

    override val sensorVelocity get() = model.fromNativeUnitVelocity(canEncoder.velocity.nativeUnits.div(1.minute))

    override fun set(controlType: ControlType, length: T) {
        pidController.setReference(model.toNativeUnitPosition(length.value), controlType)
    }

    override fun set(controlType: ControlType, velocity: Velocity<T>) {
        pidController.setReference(model.toNativeUnitVelocity(velocity).value, controlType)
    }

    override fun set(controlType: ControlType, velocity: Velocity<T>, outputPercent: Double) {
        pidController.setReference(model.toNativeUnitVelocity(velocity).value, controlType, 0, outputPercent)
    }

    override fun set(controlType: ControlType, length: T, outputPercent: Double) {
        pidController.setReference(model.toNativeUnitPosition(length).value, controlType, 0, outputPercent)
    }
}