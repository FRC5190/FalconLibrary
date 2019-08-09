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

@file:Suppress("KDocUnresolvedReference", "EqualsOrHashCode")

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.types.VaryInterpolatable
import kotlin.math.hypot

fun Rotation2d.toTranslation() = Translation2d(cos.meter, sin.meter)

data class Translation2d constructor(
    val x: SIUnit<Meter>,
    val y: SIUnit<Meter>
) : VaryInterpolatable<Translation2d> {

    constructor() : this(0.0.meter, 0.0.meter)

    // Vector to Translation3d
    constructor(
        distance: SIUnit<Meter> = 0.0.meter,
        rotation: Rotation2d = Rotation2d()
    ) : this(distance * rotation.cos, distance * rotation.sin)

    val norm get() = hypot(x.value, y.value).meter

    override fun interpolate(endValue: Translation2d, t: Double) = when {
        t <= 0 -> this
        t >= 1 -> endValue
        else -> Translation2d(
            x.lerp(endValue.x, t),
            y.lerp(endValue.y, t)
        )
    }

    override fun distance(other: Translation2d): Double {
        val x = this.x.value - other.x.value
        val y = this.y.value - other.y.value
        return hypot(x, y)
    }

    operator fun plus(other: Translation2d) = Translation2d(x + other.x, y + other.y)
    operator fun minus(other: Translation2d) = Translation2d(x - other.x, y - other.y)

    operator fun times(other: Rotation2d) = Translation2d(
        x * other.cos - y * other.sin,
        x * other.sin + y * other.cos
    )

    operator fun times(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(x * factor, y * factor)
    }

    operator fun div(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(x / factor, y / factor)
    }

    operator fun unaryMinus() = Translation2d(-x, -y)
}