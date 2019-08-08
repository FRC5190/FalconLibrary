package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.IMotorController
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnits
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100ms
import org.ghrobotics.lib.motors.AbstractFalconEncoder
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class FalconCTREEncoder<K : SIKey>(
    val motorController: IMotorController,
    val pidIdx: Int = 0,
    model: NativeUnitModel<K>
) : AbstractFalconEncoder<K>(model) {
    override val rawVelocity: SIUnit<NativeUnitVelocity> get() = motorController.getSelectedSensorVelocity(pidIdx).nativeUnitsPer100ms
    override val rawPosition: SIUnit<NativeUnit> get() = motorController.getSelectedSensorPosition(pidIdx).nativeUnits

    var encoderPhase by Delegates.observable(false) { _, _, newValue -> motorController.setSensorPhase(newValue) }

    override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {
        motorController.setSelectedSensorPosition(newPosition.value.roundToInt(), pidIdx, 0)
    }
}