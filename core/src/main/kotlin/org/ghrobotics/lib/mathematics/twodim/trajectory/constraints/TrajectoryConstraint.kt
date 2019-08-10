/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature

interface TrajectoryConstraint {
    fun getMaxVelocity(state: Pose2dWithCurvature): Double
    fun getMinMaxAcceleration(state: Pose2dWithCurvature, velocity: Double): MinMaxAcceleration

    data class MinMaxAcceleration(
        val minAcceleration: Double,
        val maxAcceleration: Double
    ) {
        constructor() : this(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY)

        val valid = minAcceleration <= maxAcceleration

        companion object {
            val kNoLimits =
                MinMaxAcceleration()
        }
    }
}