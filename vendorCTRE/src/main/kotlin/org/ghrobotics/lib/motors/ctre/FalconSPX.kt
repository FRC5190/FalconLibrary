/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.can.VictorSPX
import edu.wpi.first.wpilibj.DriverStation
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

/**
 * Wrapper around the VictorSPX motor controller.
 *
 * @param victorSPX The underlying motor controller.
 * @param model The native unit model.
 */
class FalconSPX<K : SIKey>(
    @Suppress("MemberVisibilityCanBePrivate") val victorSPX: VictorSPX,
    model: NativeUnitModel<K>,
) : FalconCTRE<K>(victorSPX, model) {

    /**
     * Alternate constructor where users can supply ID and native unit model.
     *
     * @param id The ID of the motor controller.
     * @param model The native unit model.
     */
    constructor(id: Int, model: NativeUnitModel<K>) : this(VictorSPX(id), model)

    /**
     * Returns the current drawn by the motor.
     */
    override val drawnCurrent: SIUnit<Ampere>
        get() {
            DriverStation.reportError("Current monitoring is not supported on the VictorSPX", false)
            return 0.0.amps
        }
}

fun <K : SIKey> falconSPX(
    victorSPX: VictorSPX,
    model: NativeUnitModel<K>,
    block: FalconSPX<K>.() -> Unit,
) = FalconSPX(victorSPX, model).also(block)

fun <K : SIKey> falconSPX(
    id: Int,
    model: NativeUnitModel<K>,
    block: FalconSPX<K>.() -> Unit,
) = FalconSPX(id, model).also(block)
