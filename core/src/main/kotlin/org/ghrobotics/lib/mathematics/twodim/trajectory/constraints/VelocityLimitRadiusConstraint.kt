/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.trajectory.constraint.TrajectoryConstraint
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

class VelocityLimitRadiusConstraint constructor(
    val point: Translation2d,
    val radius: SIUnit<Meter>,
    val velocityLimit: SIUnit<LinearVelocity>,
) : TrajectoryConstraint {

    override fun getMaxVelocityMetersPerSecond(
        poseMeters: Pose2d,
        curvatureRadPerMeter: Double,
        velocityMetersPerSecond: Double,
    ) = if (poseMeters.translation.getDistance(point) <= radius.value) {
        velocityLimit.value
    } else
        Double.POSITIVE_INFINITY

    override fun getMinMaxAccelerationMetersPerSecondSq(
        poseMeters: Pose2d?,
        curvatureRadPerMeter: Double,
        velocityMetersPerSecond: Double,
    ): TrajectoryConstraint.MinMax = TrajectoryConstraint.MinMax()
}
