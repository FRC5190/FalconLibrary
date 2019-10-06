/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.can.TalonSRX
import com.ctre.phoenix.motorcontrol.can.VictorSPX
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amp
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

class FalconSPX<K : SIKey>(
    val victorSPX: VictorSPX,
    model: NativeUnitModel<K>
) : FalconCTRE<K>(victorSPX, model) {

    override val drawnCurrent: SIUnit<Ampere>
        get() {
            println("Current monitoring is not supported on the VictorSPX")
            return 0.0.amp
        }

    constructor(id: Int, model: NativeUnitModel<K>) : this(VictorSPX(id), model)

}

fun <K: SIKey> falconSPX(
    victorSPX: VictorSPX,
    model: NativeUnitModel<K>,
    block: FalconSPX<K>.() -> Unit
) = FalconSPX(victorSPX, model).also(block)

fun <K: SIKey> falconSPX(
    id: Int,
    model: NativeUnitModel<K>,
    block: FalconSPX<K>.() -> Unit
) = FalconSPX(id, model).also(block)