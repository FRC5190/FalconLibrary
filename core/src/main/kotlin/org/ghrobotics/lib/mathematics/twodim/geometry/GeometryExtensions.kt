/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

@file:Suppress("FunctionName")

package org.ghrobotics.lib.mathematics.twodim.geometry

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import edu.wpi.first.wpilibj.geometry.Twist2d
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.meters
import kotlin.math.absoluteValue

/* Translation2d Unit-Safe Constructors */

fun Translation2d(x: SIUnit<Meter>, y: SIUnit<Meter>) =
    Translation2d(x.value, y.value)

fun Translation2d(distance: SIUnit<Meter>, angle: Rotation2d) =
    Translation2d(distance * angle.cos, distance * angle.sin)

/* Pose2d Unit-Safe Constructors */

fun Pose2d(x: SIUnit<Meter>, y: SIUnit<Meter>, angle: Rotation2d) =
    Pose2d(x.value, y.value, angle)

/* Transform2d Unit-Safe Constructors */
fun Transform2d(x: SIUnit<Meter>, y: SIUnit<Meter>, angle: Rotation2d) =
    edu.wpi.first.wpilibj.geometry.Transform2d(Translation2d(x, y), angle)

/* Translation2d Unit Accessors */
val Translation2d.x_u get() = x.meters
val Translation2d.y_u get() = y.meters

/* Interpolation */
fun Pose2d.interpolate(endValue: Pose2d, t: Double): Pose2d {
    return if (t < 0) {
        this
    } else if (t >= 1) {
        endValue
    } else {
        val twist = (endValue.relativeTo(this)).log()
        exp(twist * t)
    }
}

/* Misc Extensions */
fun Pose2d.mirror() =
    Pose2d(translation.x, 8.2296 - translation.y, -rotation)

fun Pose2d.log(): Twist2d {
    val dtheta = rotation.radians
    val halfDTheta = dtheta / 2.0
    val cosMinusOne = rotation.cos - 1.0

    val halfThetaByTanOfHalfDTheta = if (cosMinusOne.absoluteValue < kEpsilon) {
        1.0 - 1.0 / 12.0 * dtheta * dtheta
    } else {
        -(halfDTheta * rotation.sin) / cosMinusOne
    }
    val translationPart = translation.rotateBy(
        Rotation2d(
            halfThetaByTanOfHalfDTheta,
            -halfDTheta
        )
    )
    return Twist2d(translationPart.x, translationPart.y, dtheta)
}

operator fun Twist2d.times(scalar: Double) =
    Twist2d(dx * scalar, dy * scalar, dtheta * scalar)
