package org.ghrobotics.lib.wrappers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import kotlin.properties.Delegates.observable

class GenericFalonSRX(
        id: Int,
        timeout: Time = 10.millisecond
) : AbstractFalconSRX<NativeUnit>(id, timeout) {
    override var allowedClosedLoopError by observable(0.STU) { _, _, newValue ->
        configAllowableClosedloopError(
                0,
                newValue.asInt,
                timeoutInt
        )
    }
    override var motionCruiseVelocity by observable(0.STUPer100ms) { _, _, newValue ->
        configMotionCruiseVelocity(
                newValue.STUPer100ms.asInt,
                timeoutInt
        )
    }
    override var motionAcceleration by observable(0.STUPer100msPerSecond) { _, _, newValue ->
        configMotionAcceleration(
                newValue.STUPer100msPerSecond.asInt,
                timeoutInt
        )
    }
    override var sensorPosition
        get() = getSelectedSensorPosition(0).STU
        set(value) {
            setSelectedSensorPosition(value.asInt, 0, timeoutInt)
        }
    override val sensorVelocity: NativeUnitVelocity
        get() = getSelectedSensorVelocity(0).STUPer100ms

    override fun set(controlMode: ControlMode, length: NativeUnit) = set(controlMode, length.asDouble)

    override fun set(controlMode: ControlMode, velocity: NativeUnitVelocity) =
            set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(
            controlMode: ControlMode,
            velocity: NativeUnitVelocity,
            demandType: DemandType,
            outputPercent: Double
    ) = set(controlMode, velocity.STUPer100ms.asDouble, demandType, outputPercent)

}

