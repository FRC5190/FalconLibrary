/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers.ctre

import com.ctre.phoenix.motorcontrol.ControlMode
import com.ctre.phoenix.motorcontrol.DemandType
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnits
import org.ghrobotics.lib.mathematics.units.nativeunits.nativeUnitsPer100ms
import kotlin.properties.Delegates.observable

typealias LinearFalconSRX = FalconSRX<Length>
typealias AngularFalconSRX = FalconSRX<Rotation2d>

open class FalconSRX<T : SIUnit<T>>(
    id: Int,
    private val model: NativeUnitModel<T>,
    timeout: Time = 10.millisecond
) : AbstractFalconSRX<T>(id, timeout) {
    override var allowedClosedLoopError by observable(model.zero) { _, _, newValue ->
        configAllowableClosedloopError(0, model.toNativeUnitError(newValue.value).toInt(), timeoutInt)
    }
    override var motionCruiseVelocity by observable(model.zero.velocity) { _, _, newValue ->
        configMotionCruiseVelocity((model.toNativeUnitVelocity(newValue.value) / 10.0).toInt(), timeoutInt)
    }
    override var motionAcceleration by observable(model.zero.acceleration) { _, _, newValue ->
        configMotionAcceleration(
            (model.toNativeUnitAcceleration(newValue.value) / 10.0).toInt(),
            timeoutInt
        )
    }
    override var sensorPosition: T
        get() = model.fromNativeUnitPosition(getSelectedSensorPosition(0).nativeUnits)
        set(newValue) {
            setSelectedSensorPosition(model.toNativeUnitPosition(newValue.value).toInt(), 0, timeoutInt)
        }
    override val sensorVelocity get() = model.fromNativeUnitVelocity(getSelectedSensorVelocity(0).nativeUnitsPer100ms)

    override var velocity: Double
        get() = model.fromNativeUnitVelocity(getSelectedSensorVelocity(0) * 10.0)
        set(value) {
            set(ControlMode.Velocity, model.toNativeUnitVelocity(value) / 10.0)
        }

    override val activeTrajectoryPosition get() = model.fromNativeUnitPosition(getActiveTrajectoryPosition(0).nativeUnits)
    override val activeTrajectoryVelocity get() = model.fromNativeUnitVelocity(getActiveTrajectoryVelocity(0).nativeUnitsPer100ms)

    override fun set(controlMode: ControlMode, length: T) = set(controlMode, model.toNativeUnitPosition(length.value))

    override fun set(controlMode: ControlMode, velocity: Velocity<T>) =
        set(controlMode, velocity, DemandType.ArbitraryFeedForward, 0.0)

    override fun set(controlMode: ControlMode, length: T, demandType: DemandType, outputPercent: Double) {
        set(controlMode, model.toNativeUnitPosition(length.value), demandType, outputPercent)
    }

    override fun set(
        controlMode: ControlMode,
        velocity: Velocity<T>,
        demandType: DemandType,
        outputPercent: Double
    ) = set(controlMode, model.toNativeUnitVelocity(velocity.value) / 10.0, demandType, outputPercent)

    override fun setVelocityAndArbitraryFeedForward(velocity: Double, arbitraryFeedForward: Double) =
        set(
            ControlMode.Velocity,
            model.toNativeUnitVelocity(velocity) / 10.0,
            DemandType.ArbitraryFeedForward,
            arbitraryFeedForward
        )
}