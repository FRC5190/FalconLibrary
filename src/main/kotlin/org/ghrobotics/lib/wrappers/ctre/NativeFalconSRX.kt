package org.ghrobotics.lib.wrappers.ctre

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import kotlin.properties.Delegates.observable

class NativeFalconSRX(
    id: Int,
    timeout: Time = 10.millisecond
) : AbstractFalconSRX<NativeUnit>(id, timeout) {
    override var allowedClosedLoopError by observable(0.nativeUnits) { _, _, newValue ->
        configAllowableClosedloopError(0, newValue.value.toInt(), timeoutInt)
    }
    override var motionCruiseVelocity by observable(0.nativeUnitsPer100ms) { _, _, newValue ->
        configMotionCruiseVelocity(newValue.nativeUnitsPer100ms.toInt(), timeoutInt)
    }
    override var motionAcceleration by observable(0.nativeUnitsPer100msPerSecond) { _, _, newValue ->
        configMotionAcceleration(newValue.nativeUnitsPer100msPerSecond.toInt(), timeoutInt)
    }
    override var sensorPosition
        get() = getSelectedSensorPosition(0).nativeUnits
        set(value) {
            setSelectedSensorPosition(value.value.toInt(), 0, timeoutInt)
        }
    override val sensorVelocity get() = getSelectedSensorVelocity(0).nativeUnitsPer100ms

    override var velocity: Double
        get() = getSelectedSensorVelocity(0) * 10.0
        set(value) {
            set(ControlMode.Velocity, value / 10.0)
        }

    override val activeTrajectoryPosition get() = getActiveTrajectoryPosition(0).nativeUnits
    override val activeTrajectoryVelocity get() = getActiveTrajectoryVelocity(0).nativeUnitsPer100ms

    override fun set(controlMode: ControlMode, length: NativeUnit) = set(controlMode, length.value)

    override fun set(controlMode: ControlMode, velocity: NativeUnitVelocity) =
        set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(controlMode: ControlMode, length: NativeUnit, demandType: DemandType, outputPercent: Double) {
        set(controlMode, length.value, demandType, outputPercent)
    }

    override fun set(
        controlMode: ControlMode,
        velocity: NativeUnitVelocity,
        demandType: DemandType,
        outputPercent: Double
    ) = set(controlMode, velocity.nativeUnitsPer100ms, demandType, outputPercent)

    override fun setVelocityAndArbitraryFeedForward(velocity: Double, arbitraryFeedForward: Double) =
        set(
            ControlMode.Velocity,
            velocity / 10.0,
            DemandType.ArbitraryFeedForward,
            arbitraryFeedForward
        )
}
