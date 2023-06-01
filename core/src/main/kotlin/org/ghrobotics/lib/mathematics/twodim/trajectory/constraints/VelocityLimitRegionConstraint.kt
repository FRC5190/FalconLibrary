/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

@file:Suppress("unused")

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.trajectory.constraint.TrajectoryConstraint
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

class VelocityLimitRegionConstraint constructor(
    val region: Rectangle2d,
    val velocityLimit: SIUnit<LinearVelocity>,
) : TrajectoryConstraint {

    override fun getMaxVelocityMetersPerSecond(
        poseMeters: Pose2d,
        curvatureRadPerMeter: Double,
        velocityMetersPerSecond: Double,
    ) = if (poseMeters.translation in region) velocityLimit.value else Double.POSITIVE_INFINITY

    override fun getMinMaxAccelerationMetersPerSecondSq(
        poseMeters: Pose2d?,
        curvatureRadPerMeter: Double,
        velocityMetersPerSecond: Double,
    ) = TrajectoryConstraint.MinMax()
}
