/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import kotlin.properties.Delegates.observable

typealias FalconLengthSRX = FalconSRX<Length>

class FalconSRX<T : SIUnit<T>>(
        id: Int,
        val nativeUnitModel: NativeUnitModel<T>,
        timeout: Time = 10.millisecond
) : AbstractFalconSRX<T>(id, timeout) {
    override var allowedClosedLoopError: T by observable(nativeUnitModel.zero) { _, _, newValue ->
        configAllowableClosedloopError(
                0,
                nativeUnitModel.fromModel(newValue).value.toInt(),
                timeoutInt
        )
    }

    override var motionCruiseVelocity: SIFrac11<T, Time> by observable(
            nativeUnitModel.zero / 1.second
    ) { _, _, newValue ->
        configMotionCruiseVelocity(
                newValue.fromModel(nativeUnitModel).STUPer100ms.toInt(),
                timeoutInt
        )
    }
    override var motionAcceleration: SIFrac12<T, Time, Time> by observable(
            nativeUnitModel.zero / 1.second / 1.second
    ) { _, _, newValue ->
        configMotionAcceleration(
                newValue.fromModel(nativeUnitModel).STUPer100msPerSecond.toInt(),
                timeoutInt
        )
    }
    override var sensorPosition: T
        get() = getSelectedSensorPosition(0).STU.toModel(nativeUnitModel)
        set(value) {
            setSelectedSensorPosition(value.fromModel(nativeUnitModel).value.toInt(), 0, timeoutInt)
        }
    override val sensorVelocity: SIFrac11<T, Time>
        get() = getSelectedSensorVelocity(0).STUPer100ms.toModel(nativeUnitModel)

    override fun set(controlMode: ControlMode, length: T) = set(controlMode, length.fromModel(nativeUnitModel).value)

    override fun set(controlMode: ControlMode, velocity: SIFrac11<T, Time>) =
            set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(
            controlMode: ControlMode,
            velocity: SIFrac11<T, Time>,
            demandType: DemandType,
            outputPercent: Double
    ) = set(controlMode, velocity.fromModel(nativeUnitModel).STUPer100ms, demandType, outputPercent)


}


