/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

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

/**
 * Represents the encoder connected to a CTRE motor controller.
 *
 * @param motorController The motor controller.
 * @param pidIdx The PID ID.
 * @param model The native unit model.
 */
class FalconCTREEncoder<K : SIKey>(
    private val motorController: IMotorController,
    private val pidIdx: Int = 0,
    model: NativeUnitModel<K>,
) : AbstractFalconEncoder<K>(model) {
    /**
     * Returns the raw velocity from the encoder.
     */
    override val rawVelocity: SIUnit<NativeUnitVelocity> get() = motorController.getSelectedSensorVelocity(pidIdx).nativeUnitsPer100ms

    /**
     * Returns the raw position from the encoder.
     */
    override val rawPosition: SIUnit<NativeUnit> get() = motorController.getSelectedSensorPosition(pidIdx).nativeUnits

    /**
     * Sets the encoder phase for the encoder.
     */
    var encoderPhase by Delegates.observable(false) { _, _, newValue -> motorController.setSensorPhase(newValue) }

    /**
     * Resets the encoder position to a certain value.
     *
     * @param newPosition The position to reset to.
     */
    override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {
        motorController.setSelectedSensorPosition(newPosition.value.roundToInt().toDouble(), pidIdx, 0)
    }
}
