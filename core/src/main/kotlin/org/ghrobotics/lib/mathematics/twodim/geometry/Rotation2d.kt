package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon

class Rotation2d {

    val value: Double
    val cos: Double
    val sin: Double

    constructor() : this(0.0)

    constructor(value: Double) : this(Math.cos(value), Math.sin(value), true)

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
        value = Math.atan2(sin, cos)
    }

    val radian get() = value
    val degree get() = Math.toDegrees(value)

    fun isParallel(rotation: Rotation2d) = (this - rotation).radian epsilonEquals 0.0

    operator fun minus(other: Rotation2d) = plus(-other)
    operator fun unaryMinus() = Rotation2d(-value)

    operator fun times(other: Double) = Rotation2d(value * other)

    operator fun plus(other: Rotation2d): Rotation2d {
        return Rotation2d(
            cos * other.cos - sin * other.sin,
            cos * other.sin + sin * other.cos,
            true
        )
    }

    override fun equals(other: Any?): Boolean {
        return other is Rotation2d && other.value epsilonEquals value
    }

    companion object {
        fun fromDegrees(x: Double) = Rotation2d(Math.toRadians(x))
    }
}