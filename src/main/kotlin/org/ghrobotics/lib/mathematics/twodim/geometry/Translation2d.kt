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

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.types.VaryInterpolatable

fun Rotation2d.toTranslation() = Translation2d(cos.meter, sin.meter)

data class Translation2d(
    val x: Length = 0.meter,
    val y: Length = 0.meter
) : VaryInterpolatable<Translation2d> {

    val norm get() = Math.hypot(x.value, y.value).meter

    override fun interpolate(endValue: Translation2d, t: Double) = when {
        t <= 0 -> this
        t >= 1 -> endValue
        else -> Translation2d(
            x.lerp(endValue.x, t),
            y.lerp(endValue.y, t)
        )
    }

    override fun distance(other: Translation2d) = (-this + other).norm.value

    operator fun plus(other: Translation2d) = Translation2d(
        x + other.x,
        y + other.y
    )

    operator fun minus(other: Translation2d) = Translation2d(
        x - other.x,
        y - other.y
    )

    operator fun times(other: Rotation2d) = Translation2d(
        x * other.cos - y * other.sin,
        x * other.sin + y * other.cos
    )

    operator fun times(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(
            x * factor,
            y * factor
        )
    }

    operator fun div(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(
            x / factor,
            y / factor
        )
    }

    operator fun unaryMinus() = Translation2d(-x, -y)
}