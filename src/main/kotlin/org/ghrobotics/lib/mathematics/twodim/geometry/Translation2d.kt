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

import org.ghrobotics.lib.mathematics.lerp
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.types.VaryInterpolatable

fun Rotation2d.toTranslation() = Translation2d(cos, sin)

data class Translation2d(
        var xRaw: Double = 0.0,
        var yRaw: Double = 0.0
) : VaryInterpolatable<Translation2d> {

    var x: Length
        get() = xRaw.meter
        set(value) {
            xRaw = value.meter
        }

    var y: Length
        get() = yRaw.meter
        set(value) {
            yRaw = value.meter
        }

    constructor(
            x: Length,
            y: Length
    ) : this(
            x.meter,
            y.meter
    )

    val norm
        get() = Math.hypot(xRaw, yRaw)

    override fun interpolate(endValue: Translation2d, t: Double) = when {
        t <= 0 -> this
        t >= 1 -> endValue
        else -> Translation2d(
                xRaw.lerp(endValue.xRaw, t),
                yRaw.lerp(endValue.yRaw, t)
        )
    }

    override fun distance(other: Translation2d) = (-this + other).norm

    operator fun plus(other: Translation2d) = Translation2d(
            xRaw + other.xRaw,
            yRaw + other.yRaw
    )

    operator fun minus(other: Translation2d) = Translation2d(
            xRaw - other.xRaw,
            yRaw - other.yRaw
    )

    operator fun times(other: Rotation2d) = Translation2d(
            xRaw * other.cos - yRaw * other.sin,
            xRaw * other.sin + yRaw * other.cos
    )

    operator fun times(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(
                xRaw * factor,
                yRaw * factor
        )
    }

    operator fun div(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(
                xRaw / factor,
                yRaw / factor
        )
    }

    operator fun unaryMinus() = Translation2d(-xRaw, -yRaw)

    companion object {
        fun cross(a: Translation2d, b: Translation2d) = a.xRaw * b.yRaw - a.yRaw * b.xRaw
    }
}