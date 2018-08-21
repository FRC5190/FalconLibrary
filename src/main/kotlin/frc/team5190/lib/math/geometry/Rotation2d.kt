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

package frc.team5190.lib.math.geometry

import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.math.geometry.interfaces.IRotation2d
import frc.team5190.lib.kEpsilon
import java.text.DecimalFormat


class Rotation2d : IRotation2d<Rotation2d> {

    val cos: Double
    val sin: Double

    val tan: Double
        get() {
            return if (Math.abs(cos) < kEpsilon) {
                if (sin >= 0.0) {
                    java.lang.Double.POSITIVE_INFINITY
                } else {
                    java.lang.Double.NEGATIVE_INFINITY
                }
            } else sin / cos
        }


    private var unboundedDegrees = 0.0


    val radians: Double
        get() = Math.atan2(sin, cos)

    val degrees: Double
        get() = Math.toDegrees(radians)

    val normal: Rotation2d
        get() {
            return Rotation2d(-sin, cos, false)
        }

    val inverse: Rotation2d
        get() {
            return Rotation2d(cos, -sin, false)
        }

    override val rotation: Rotation2d
        get() = this


    constructor(x: Double = 1.0, y: Double = 0.0, normalize: Boolean = false) {
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
        unboundedDegrees = Math.toDegrees(Math.atan2(sin, cos))
    }

    constructor(other: Rotation2d) {
        cos = other.cos
        sin = other.sin
        unboundedDegrees = Math.toDegrees(Math.atan2(sin, cos))
    }

    constructor(theta_degrees: Double) {
        cos = Math.cos(Math.toRadians(theta_degrees))
        sin = Math.sin(Math.toRadians(theta_degrees))
        this.unboundedDegrees = theta_degrees
    }

    constructor(direction: Translation2d, normalize: Boolean) : this(direction.x, direction.y, normalize)


    fun rotateBy(other: Rotation2d): Rotation2d {
        return Rotation2d(cos * other.cos - sin * other.sin,
                cos * other.sin + sin * other.cos, true)
    }


    fun isParallel(other: Rotation2d): Boolean {
        return Translation2d.cross(toTranslation(), other.toTranslation()) epsilonEquals 0.0
    }

    fun toTranslation(): Translation2d {
        return Translation2d(cos, sin)
    }

    operator fun plus(other: Rotation2d) = this.rotateBy(other)
    operator fun minus(other: Rotation2d) = this.rotateBy(other.inverse)

    override fun interpolate(upperVal: Rotation2d, interpolatePoint: Double): Rotation2d {
        if (interpolatePoint <= 0) {
            return Rotation2d(this)
        } else if (interpolatePoint >= 1) {
            return Rotation2d(upperVal)
        }
        val angleDiff = inverse.rotateBy(upperVal).radians
        return this.rotateBy(Rotation2d.fromRadians(angleDiff * interpolatePoint))
    }

    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return "(" + fmt.format(degrees) + " deg)"
    }

    override fun toCSV(): String {
        val fmt = DecimalFormat("#0.000")
        return fmt.format(degrees)
    }

    override fun distance(other: Rotation2d): Double {
        return inverse.rotateBy(other).radians
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Rotation2d) false else distance(other) < kEpsilon
    }

    companion object {
        private val kIdentity = Rotation2d()

        fun identity(): Rotation2d {
            return kIdentity
        }

        fun fromRadians(angleRadians: Double): Rotation2d {
            return Rotation2d(Math.cos(angleRadians), Math.sin(angleRadians), false)
        }

        fun fromDegrees(angleDegrees: Double): Rotation2d {
            return Rotation2d(angleDegrees)
        }
    }
}
