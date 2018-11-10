/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

@file:Suppress("unused", "EqualsOrHashCode")

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.lerp
import org.ghrobotics.lib.mathematics.units.derivedunits.Curvature
import org.ghrobotics.lib.types.Interpolatable
import org.ghrobotics.lib.types.VaryInterpolatable

data class Pose2dWithCurvature(
    val pose: Pose2d,
    val curvature: Pose2dCurvature
) : VaryInterpolatable<Pose2dWithCurvature> {

    val mirror
        get() = Pose2dWithCurvature(
            pose.mirror,
            -curvature
        )

    override fun interpolate(endValue: Pose2dWithCurvature, t: Double) =
        Pose2dWithCurvature(
            pose.interpolate(endValue.pose, t),
            curvature.interpolate(endValue.curvature, t)
        )

    override fun distance(other: Pose2dWithCurvature) = pose.distance(other.pose)

    operator fun plus(other: Pose2d) = Pose2dWithCurvature(
        this.pose + other,
        curvature
    )
}

/**
 * @param curvature 1/m
 * @param dkds derivative of curvature
 */
data class Pose2dCurvature(
    val curvature: Curvature,
    val dkds: Double
) : Interpolatable<Pose2dCurvature> {
    override fun interpolate(endValue: Pose2dCurvature, t: Double) =
        Pose2dCurvature(
            curvature.lerp(endValue.curvature, t),
            dkds.lerp(endValue.dkds, t)
        )

    operator fun unaryMinus() = Pose2dCurvature(
        -curvature,
        -dkds
    )
}
