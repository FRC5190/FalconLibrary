/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import kotlin.properties.Delegates.observable

typealias FalconLengthSRX = FalconSRX<Length>

class FalconSRX<T : SIValue<T>>(
        id: Int,
        val nativeUnitModel: NativeUnitModel<T>,
        timeout: Time = 10.millisecond
) : AbstractFalconSRX<T>(id, timeout) {
    override var allowedClosedLoopError: T by observable(nativeUnitModel.zero) { _, _, newValue ->
        configAllowableClosedloopError(
                0,
                nativeUnitModel.fromModel(newValue).asInt,
                timeoutInt
        )
    }

    override var motionCruiseVelocity: SIFrac11<T, Time> by observable(
            SIFrac11(
                    nativeUnitModel.zero,
                    0.second
            )
    ) { _, _, newValue ->
        configMotionCruiseVelocity(
                newValue.fromModel(nativeUnitModel).STUPer100ms.asInt,
                timeoutInt
        )
    }
    override var motionAcceleration: SIFrac12<T, Time, Time> by observable(
            SIFrac12(
                    nativeUnitModel.zero,
                    SIExp2(0.second, 0.second)
            )
    ) { _, _, newValue ->
        configMotionAcceleration(
                newValue.fromModel(nativeUnitModel).STUPer100msPerSecond.asInt,
                timeoutInt
        )
    }
    override var sensorPosition: T
        get() = getSelectedSensorPosition(0).STU.toModel(nativeUnitModel)
        set(value) {
            setSelectedSensorPosition(value.fromModel(nativeUnitModel).asInt, 0, timeoutInt)
        }
    override val sensorVelocity: SIFrac11<T, Time>
        get() = getSelectedSensorVelocity(0).STUPer100ms.toModel(nativeUnitModel)

    override fun set(controlMode: ControlMode, length: T) = set(controlMode, length.fromModel(nativeUnitModel).asDouble)

    override fun set(controlMode: ControlMode, velocity: SIFrac11<T, Time>) =
            set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(
            controlMode: ControlMode,
            velocity: SIFrac11<T, Time>,
            demandType: DemandType,
            outputPercent: Double
    ) = set(controlMode, velocity.fromModel(nativeUnitModel).STUPer100ms.asDouble, demandType, outputPercent)


}


