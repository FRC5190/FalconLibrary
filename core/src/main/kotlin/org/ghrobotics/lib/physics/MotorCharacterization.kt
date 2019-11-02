/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.physics

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.operations.times
import kotlin.math.sign

/**
 * Represents a gearbox that was characterized for kV, kA, and kS parameters.
 * All units are SI.
 *
 * @param K The type of unit to use. This is either Meter or Radian.
 *
 * @param kV The characterization constant for velocity. The represents the voltage
 *           required for the robot to travel a certain velocity at steady state.
 * @param kA The characterization constant for acceleration. This represents the
 *           voltage required for the robot to acceleration a certain amount.
 * @param kS The voltage required to break static friction.
 */
class MotorCharacterization<K : SIKey>(
    val kV: SIUnit<Frac<Volt, Velocity<K>>>,
    val kA: SIUnit<Frac<Volt, Acceleration<K>>>,
    val kS: SIUnit<Volt>
) {
    /**
     * Returns the feedforward voltage.
     *
     * @param velocity The desired velocity.
     * @param acceleration The desired acceleration.
     *
     * @return The feedforward voltage.
     */
    fun getVoltage(
        velocity: SIUnit<Velocity<K>>,
        acceleration: SIUnit<Acceleration<K>>
    ): SIUnit<Volt> {
        return (kV * velocity) + (kA * acceleration) + kS * sign(velocity.value)
    }
}
