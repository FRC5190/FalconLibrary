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
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.types.VaryInterpolatable

fun Rotation2d.toTranslation() = Translation2d(cos, sin)

data class Translation2d internal constructor(
    internal val _x: Double,
    internal val _y: Double
) : VaryInterpolatable<Translation2d> {

    val x get() = _x.meter
    val y get() = _y.meter

    constructor() : this(0.0, 0.0)

    constructor(
        x: Length = 0.meter,
        y: Length = 0.meter
    ) : this(x.value, y.value)

    // Vector to Translation2d
    constructor(distance: Length = 0.meter, rotation: Rotation2d = 0.degree)
        : this(distance * rotation.cos, distance * rotation.sin)

    internal val _norm get() = Math.hypot(_x, _y)
    val norm get() = _norm.meter

    override fun interpolate(endValue: Translation2d, t: Double) = when {
        t <= 0 -> this
        t >= 1 -> endValue
        else -> Translation2d(
            _x.lerp(endValue._x, t),
            _y.lerp(endValue._y, t)
        )
    }

    override fun distance(other: Translation2d) = (-this + other)._norm
    operator fun plus(other: Translation2d) = Translation2d(_x + other._x, _y + other._y)
    operator fun minus(other: Translation2d) = Translation2d(_x - other._x, _y - other._y)

    operator fun times(other: Rotation2d) = Translation2d(
        _x * other.cos - _y * other.sin,
        _x * other.sin + _y * other.cos
    )

    operator fun times(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(_x * factor, _y * factor)
    }

    operator fun div(other: Number): Translation2d {
        val factor = other.toDouble()
        return Translation2d(_x / factor, _y / factor)
    }

    operator fun unaryMinus() = Translation2d(-_x, -_y)
}