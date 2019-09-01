/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.lerp
import kotlin.math.hypot

data class Pose2dWithCurvature(
    val pose: Pose2d,
    val curvature: Double,
    val dkds: Double
) {
    fun mirror(): Pose2dWithCurvature {
        return Pose2dWithCurvature(pose.mirror(), -curvature, -dkds)
    }

    fun interpolate(endValue: Pose2dWithCurvature, t: Double): Pose2dWithCurvature {
        return Pose2dWithCurvature(
            pose.interpolate(endValue.pose, t),
            curvature.lerp(endValue.curvature, t),
            dkds.lerp(endValue.dkds, t)
        )
    }

    fun distance(other: Pose2dWithCurvature): Double {
        val twist = other.pose.relativeTo(pose).log()
        return hypot(twist.dx, twist.dy)
    }
}