package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.IMotorController
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import org.ghrobotics.lib.motors.AbstractFalconEncoder
import kotlin.properties.Delegates

class FalconCTREEncoder<T : SIUnit<T>>(
    val motorController: IMotorController,
    val pidIdx: Int = 0,
    model: NativeUnitModel<T>
) : AbstractFalconEncoder<T>(model) {
    override val rawVelocity: Double get() = motorController.getSelectedSensorVelocity(pidIdx).toDouble() * 10.0
    override val rawPosition: Double get() = motorController.getSelectedSensorPosition(pidIdx).toDouble()

    var encoderPhase by Delegates.observable(false) { _, _, newValue -> motorController.setSensorPhase(newValue) }

    override fun resetPosition(newPosition: Double) {
        motorController.setSelectedSensorPosition(0, pidIdx, 0)
    }
}