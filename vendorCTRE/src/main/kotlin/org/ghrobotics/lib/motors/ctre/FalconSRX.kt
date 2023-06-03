/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration
import com.ctre.phoenix.motorcontrol.TalonSRXFeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.inAmps
import org.ghrobotics.lib.mathematics.units.inMilliseconds
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import kotlin.properties.Delegates

/**
 * Wrapper around the TalonSRX motor controller.
 *
 * @param talonSRX The underlying TalonSRX motor controller.
 * @param model The native unit model.
 */
@Suppress("Unused")
class FalconSRX<K : SIKey>(
    @Suppress("MemberVisibilityCanBePrivate") val talonSRX: TalonSRX,
    model: NativeUnitModel<K>,
) : FalconCTRE<K>(talonSRX, model) {

    /**
     * Alternate constructor where users can supply ID and native unit model.
     *
     * @param id The ID of the motor controller.
     * @param model The native unit model.
     */
    constructor(id: Int, model: NativeUnitModel<K>) : this(TalonSRX(id), model)

    /**
     * Returns the current drawn by the motor.
     */
    override val drawnCurrent: SIUnit<Ampere>
        get() = talonSRX.supplyCurrent.amps

    /**
     * Configures the feedback device for the motor controller.
     */
    var feedbackDevice by Delegates.observable(TalonSRXFeedbackDevice.QuadEncoder) { _, _, newValue ->
        talonSRX.configSelectedFeedbackSensor(newValue, 0, 0)
    }

    /**
     * Configure the supply-side current limit for the motor.
     *
     * @param config The supply-side current limit configuration.
     */
    fun configSupplyCurrentLimit(config: SupplyCurrentLimitConfiguration) {
        talonSRX.configSupplyCurrentLimit(config, 0)
    }

    /**
     * Configure the current limit for the motor.
     *
     * @param enabled Whether current limiting should be enabled.
     * @param config The current limiting configuration.
     */
    @Deprecated("This method has been deprecated.", ReplaceWith("configSupplyCurrentLimit"))
    fun configCurrentLimit(enabled: Boolean, config: CurrentLimitConfig) {
        talonSRX.enableCurrentLimit(enabled)
        if (enabled) {
            talonSRX.configPeakCurrentLimit(config.peakCurrentLimit.inAmps().toInt())
            talonSRX.configPeakCurrentDuration(config.peakCurrentLimitDuration.inMilliseconds().toInt())
            talonSRX.configContinuousCurrentLimit(config.continuousCurrentLimit.inAmps().toInt())
        }
    }

    /**
     * Represents the TalonSRX current limit configuration.
     *
     * @param peakCurrentLimit The peak current limit.
     * @param peakCurrentLimitDuration How long the peak current can hold.
     * @param continuousCurrentLimit What the current should be dropped to.
     */
    @Deprecated(
        "This data class has been deprecated.",
        ReplaceWith(
            "SupplyCurrentLimitConfiguration",
            "import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration",
        ),
    )
    data class CurrentLimitConfig(
        val peakCurrentLimit: SIUnit<Ampere>,
        val peakCurrentLimitDuration: SIUnit<Second>,
        val continuousCurrentLimit: SIUnit<Ampere>,
    )
}

fun <K : SIKey> falconSRX(
    talonSRX: TalonSRX,
    model: NativeUnitModel<K>,
    block: FalconSRX<K>.() -> Unit,
) = FalconSRX(talonSRX, model).also(block)

fun <K : SIKey> falconSRX(
    id: Int,
    model: NativeUnitModel<K>,
    block: FalconSRX<K>.() -> Unit,
) = FalconSRX(id, model).also(block)
