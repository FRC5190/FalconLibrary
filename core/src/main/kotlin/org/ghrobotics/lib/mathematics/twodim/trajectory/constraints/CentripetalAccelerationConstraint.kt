/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import kotlin.math.absoluteValue
import kotlin.math.sqrt

class CentripetalAccelerationConstraint constructor(
    val mMaxCentripetalAcceleration: SIUnit<LinearAcceleration>
) : TrajectoryConstraint {

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        sqrt((mMaxCentripetalAcceleration.value / state.curvature).absoluteValue)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ) = TrajectoryConstraint.MinMaxAcceleration.kNoLimits
}