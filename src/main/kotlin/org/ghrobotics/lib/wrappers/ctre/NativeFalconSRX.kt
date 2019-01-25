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
    override var allowedClosedLoopError by observable(0.STU) { _, _, newValue ->
        configAllowableClosedloopError(0, newValue.value.toInt(), timeoutInt)
    }
    override var motionCruiseVelocity by observable(0.STUPer100ms) { _, _, newValue ->
        configMotionCruiseVelocity(newValue.STUPer100ms.toInt(), timeoutInt)
    }
    override var motionAcceleration by observable(0.STUPer100msPerSecond) { _, _, newValue ->
        configMotionAcceleration(newValue.STUPer100msPerSecond.toInt(), timeoutInt)
    }
    override var sensorPosition
        get() = getSelectedSensorPosition(0).STU
        set(value) {
            setSelectedSensorPosition(value.value.toInt(), 0, timeoutInt)
        }
    override val sensorVelocity get() = getSelectedSensorVelocity(0).STUPer100ms

    override val activeTrajectoryPosition get() = getActiveTrajectoryPosition(0).STU
    override val activeTrajectoryVelocity get() = getActiveTrajectoryVelocity(0).STUPer100ms
    
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
    ) = set(controlMode, velocity.STUPer100ms, demandType, outputPercent)
}
