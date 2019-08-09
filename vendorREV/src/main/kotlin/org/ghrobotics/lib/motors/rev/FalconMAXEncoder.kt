/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.rev

import com.revrobotics.CANEncoder
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity
import org.ghrobotics.lib.motors.AbstractFalconEncoder

class FalconMAXEncoder<K : SIKey>(
    val canEncoder: CANEncoder,
    model: NativeUnitModel<K>
) : AbstractFalconEncoder<K>(model) {
    override val rawVelocity: SIUnit<NativeUnitVelocity> get() = SIUnit(canEncoder.velocity / 60.0)
    override val rawPosition: SIUnit<NativeUnit> get() = SIUnit(canEncoder.position)

    override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {
        canEncoder.position = newPosition.value
    }

}
