/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity

interface FalconEncoder<K : SIKey> {

    /**
     * Sets the simulated [position] of this motor.
     */
    fun setSimulatedPosition(position: SIUnit<K>)

    /**
     * Sets the simulated [velocity] of this motor.
     */
    fun setSimulatedVelocity(position: SIUnit<K>)

    /**
     * The velocity of the encoder in [K]/s. When in a simulation, returns the simulated velocity.
     */
    val velocity: SIUnit<Velocity<K>>
    /**
     * The position of the encoder in [K]. When in a simulation, returns the simulated position.
     */
    val position: SIUnit<K>

    /**
     * The velocity of the encoder in NativeUnits/s
     */
    val rawVelocity: SIUnit<NativeUnitVelocity>
    /**
     * The position of the encoder in NativeUnits
     */
    val rawPosition: SIUnit<NativeUnit>

    /**
     * Reset the position of the encoder. Does not work in a simulation.
     */
    fun resetPosition(newPosition: SIUnit<K>)

    fun resetPositionRaw(newPosition: SIUnit<NativeUnit>)
}
