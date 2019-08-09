/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

interface TimingConstraint<S> {
    fun getMaxVelocity(state: S): Double

    fun getMinMaxAcceleration(state: S, velocity: Double): MinMaxAcceleration

    data class MinMaxAcceleration(
        val minAcceleration: Double,
        val maxAcceleration: Double
    ) {
        constructor() : this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

        val valid = minAcceleration <= maxAcceleration

        companion object {
            val kNoLimits = MinMaxAcceleration()
        }
    }
}