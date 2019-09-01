/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.AngularAcceleration
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class AngularAccelerationConstraint constructor(
    val maxAngularAcceleration: SIUnit<AngularAcceleration>
) : TrajectoryConstraint {

    init {
        require(maxAngularAcceleration.value >= 0) { "Cannot have negative Angular Acceleration." }
    }

    override fun getMaxVelocity(state: Pose2dWithCurvature): Double {
        /**
         * We don't want v^2 * dk/ds alone to go over the max angular acceleration.
         * v^2 * dk/ds = maxAngularAcceleration when linear acceleration = 0.
         * v = sqrt(maxAngularAcceleration / dk/ds)
         */

        return sqrt(maxAngularAcceleration.value / state.dkds.absoluteValue)
    }

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TrajectoryConstraint.MinMaxAcceleration {

        /**
         * We want to limit the acceleration such that we never go above the specified angular acceleration.
         *
         * Angular acceleration = dw/dt     WHERE   w = omega = angular velocity
         * w = v * k                        WHERE   v = linear velocity, k = curvature
         *
         * dw/dt = d/dt (v * k)
         *
         * By chain rule,
         * dw/dt = dv/dt * k + v * dk/dt   [1]
         *
         * We don't have dk/dt, but we do have dk/ds and ds/dt
         * dk/ds * ds/dt = dk/dt     [2]
         *
         * Substituting [2] in [1], we get
         * dw/dt = acceleration * curvature + velocity * velocity * d_curvature
         * WHERE acceleration = dv/dt, velocity = ds/dt, d_curvature = dk/dt and curvature = k
         *
         * We now want to find the linear acceleration such that the angular acceleration (dw/dt) never goes above
         * the specified amount.
         *
         * acceleration * curvature = dw/dt - (velocity * velocity * d_curvature)
         * acceleration = (dw/dt - (velocity * velocity * d_curvature)) / curvature
         *
         * Yay Calculus
         */

        val maxAbsoluteAcceleration =
            abs((maxAngularAcceleration.value - (velocity * velocity * state.dkds)) / state.curvature)

        return TrajectoryConstraint.MinMaxAcceleration(-maxAbsoluteAcceleration, maxAbsoluteAcceleration)
    }
}