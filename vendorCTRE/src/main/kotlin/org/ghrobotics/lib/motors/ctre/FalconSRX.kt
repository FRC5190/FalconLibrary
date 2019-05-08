package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.ElectricCurrent
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitModel
import kotlin.properties.Delegates

class FalconSRX<T : SIUnit<T>>(
    val talonSRX: TalonSRX,
    model: NativeUnitModel<T>
) : FalconCTRE<T>(talonSRX, model) {

    constructor(id: Int, model: NativeUnitModel<T>) : this(TalonSRX(id), model)

    var feedbackSensor by Delegates.observable(FeedbackDevice.QuadEncoder) { _, _, newValue ->
        talonSRX.configSelectedFeedbackSensor(newValue, 0, 0)
    }

    fun configCurrentLimit(enabled: Boolean, config: CurrentLimitConfig) {
        talonSRX.enableCurrentLimit(enabled)
        if (enabled) {
            talonSRX.configPeakCurrentLimit(config.peakCurrentLimit.amp.toInt())
            talonSRX.configPeakCurrentDuration(config.peakCurrentLimitDuration.millisecond.toInt())
            talonSRX.configContinuousCurrentLimit(config.continuousCurrentLimit.amp.toInt())
        }
    }

    data class CurrentLimitConfig(
        val peakCurrentLimit: ElectricCurrent,
        val peakCurrentLimitDuration: Time,
        val continuousCurrentLimit: ElectricCurrent
    )


}