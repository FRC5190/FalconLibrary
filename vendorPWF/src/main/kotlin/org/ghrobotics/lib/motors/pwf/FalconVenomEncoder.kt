/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.pwf

import com.playingwithfusion.CANVenom
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity
import org.ghrobotics.lib.motors.AbstractFalconEncoder

/**
 * Represents an encoder connected to the Venom motor controller.
 */
class FalconVenomEncoder<K : SIKey>(
    private val venom: CANVenom,
    model: NativeUnitModel<K>,
) : AbstractFalconEncoder<K>(model) {
    /**
     * Returns the raw velocity from the encoder.
     */
    override val rawVelocity: SIUnit<NativeUnitVelocity>
        get() = SIUnit(venom.speed / 60.0)

    /**
     * Returns the raw position from the encoder.
     */
    override val rawPosition: SIUnit<NativeUnit>
        get() = SIUnit(venom.position)

    /**
     * Resets the encoder position to a certain value.
     *
     * @param newPosition The position to reset to.
     */
    override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {
        venom.position = newPosition.value
    }
}
