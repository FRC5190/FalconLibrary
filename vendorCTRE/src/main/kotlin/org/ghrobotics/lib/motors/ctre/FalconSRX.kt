/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.amp
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import kotlin.properties.Delegates

class FalconSRX<K : SIKey>(
    val talonSRX: TalonSRX,
    model: NativeUnitModel<K>
) : FalconCTRE<K>(talonSRX, model) {

    constructor(id: Int, model: NativeUnitModel<K>) : this(TalonSRX(id), model)

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
        val peakCurrentLimit: SIUnit<Ampere>,
        val peakCurrentLimitDuration: SIUnit<Second>,
        val continuousCurrentLimit: SIUnit<Ampere>
    )


}