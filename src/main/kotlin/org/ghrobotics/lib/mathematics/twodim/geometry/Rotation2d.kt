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

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon

val Number.degrees
    get() = Rotation2d.fromDegrees(toDouble())
val Number.radians
    get() = Rotation2d(toDouble())

/**
 * @param angle in radians
 */
class Rotation2d {

    val radians: Double
    val cos: Double
    val sin: Double

    constructor(angle: Double = 0.0) {
        this.radians = angle % (Math.PI * 2)
        cos = Math.cos(angle)
        sin = Math.sin(angle)
    }

    constructor(x: Double, y: Double, normalize: Boolean) {
        if (normalize) {
            val magnitude = Math.hypot(x, y)
            if (magnitude > kEpsilon) {
                sin = y / magnitude
                cos = x / magnitude
            } else {
                sin = 0.0
                cos = 1.0
            }
        } else {
            cos = x
            sin = y
        }
        radians = Math.atan2(sin, cos) % (Math.PI * 2)
    }

    val degrees
        get() = Math.toDegrees(radians)

    val toVector
        get() = Translation2d(cos, sin)

    fun isParallel(other: Rotation2d) = Translation2d.cross(toVector, other.toVector) epsilonEquals 0.0

    operator fun plus(other: Rotation2d) = Rotation2d(
        cos * other.cos - sin * other.sin,
        cos * other.sin + sin * other.cos,
        true
    )

    operator fun minus(other: Rotation2d) = plus(-other)

    operator fun unaryMinus() = Rotation2d(cos, -sin, false)

    companion object {
        fun fromDegrees(angle: Double) = Rotation2d(Math.toRadians(angle))
    }
}
