/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

/* ktlint-disable no-wildcard-imports */
import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import kotlin.properties.Delegates.observable

typealias LinearFalconSRX = FalconSRX<Length>
typealias AngularFalconSRX = FalconSRX<Rotation2d>

class FalconSRX<T : SIUnit<T>>(
    id: Int,
    private val model: NativeUnitModel<T>,
    timeout: Time = 10.millisecond
) : AbstractFalconSRX<T>(id, timeout) {
    override var allowedClosedLoopError by observable(model.zero) { _, _, newValue ->
        configAllowableClosedloopError(0, model.fromModel(newValue).value.toInt(), timeoutInt)
    }
    override var motionCruiseVelocity by observable(model.zero.velocity) { _, _, newValue ->
        configMotionCruiseVelocity(newValue.fromModel(model).STUPer100ms.toInt(), timeoutInt)
    }
    override var motionAcceleration by observable(model.zero.acceleration) { _, _, newValue ->
        configMotionAcceleration(newValue.fromModel(model).STUPer100msPerSecond.toInt(), timeoutInt)
    }
    override var sensorPosition: T
        get() = getSelectedSensorPosition(0).STU.toModel(model)
        set(value) {
            setSelectedSensorPosition(value.fromModel(model).value.toInt(), 0, timeoutInt)
        }
    override val sensorVelocity get() = getSelectedSensorVelocity(0).STUPer100ms.toModel(model)

    override fun set(controlMode: ControlMode, length: T) = set(controlMode, length.fromModel(model).value)

    override fun set(controlMode: ControlMode, velocity: Velocity<T>) =
        set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(
        controlMode: ControlMode,
        velocity: Velocity<T>,
        demandType: DemandType,
        outputPercent: Double
    ) = set(controlMode, velocity.fromModel(model).STUPer100ms, demandType, outputPercent)
}