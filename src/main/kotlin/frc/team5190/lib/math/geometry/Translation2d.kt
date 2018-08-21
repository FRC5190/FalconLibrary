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

package frc.team5190.lib.math.geometry

import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.kEpsilon
import frc.team5190.lib.math.geometry.interfaces.ITranslation2d
import java.text.DecimalFormat


class Translation2d : ITranslation2d<Translation2d> {

    var x: Double = 0.toDouble()
    var y: Double = 0.toDouble()

    override val translation: Translation2d
        get() = this

    constructor() {
        x = 0.0
        y = 0.0
    }

    constructor(x: Double, y: Double) {
        this.x = x
        this.y = y
    }

    constructor(other: Translation2d) {
        x = other.x
        y = other.y
    }

    constructor(start: Translation2d, end: Translation2d) {
        x = end.x - start.x
        y = end.y - start.y
    }


    val norm: Double
        get() {
            return Math.hypot(x, y)
        }


    fun translateBy(other: Translation2d): Translation2d {
        return Translation2d(x + other.x, y + other.y)
    }


    fun rotateBy(rotation: Rotation2d): Translation2d {
        return Translation2d(x * rotation.cos - y * rotation.sin, x * rotation.sin + y * rotation.cos)
    }


    val inverse: Translation2d
        get() {
            return Translation2d(-x, -y)
        }

    override fun interpolate(upperVal: Translation2d, interpolatePoint: Double): Translation2d {
        if (interpolatePoint <= 0) {
            return Translation2d(this)
        } else if (interpolatePoint >= 1) {
            return Translation2d(upperVal)
        }
        return extrapolate(upperVal, interpolatePoint)
    }

    private fun extrapolate(other: Translation2d, x: Double): Translation2d {
        return Translation2d(x * (other.x - x) + x, x * (other.y - y) + y)
    }

    fun scale(s: Double): Translation2d {
        return Translation2d(x * s, y * s)
    }

    val mirror: Translation2d
        get() {
            return Translation2d(x, 27 - y)
        }

    operator fun plus(other: Translation2d) = this.translateBy(other)
    operator fun minus(other: Translation2d) = this.translateBy(other.inverse)

    infix fun epsilonEquals(other: Translation2d): Boolean {
        return x epsilonEquals other.x && y epsilonEquals other.y
    }

    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return "(" + fmt.format(x) + "," + fmt.format(y) + ")"
    }

    override fun toCSV(): String {
        val fmt = DecimalFormat("#0.000")
        return fmt.format(x) + "," + fmt.format(y)
    }

    override fun distance(other: Translation2d): Double {
        return inverse.translateBy(other).norm
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Translation2d) false else distance(other) < kEpsilon
    }

    companion object {
        fun cross(a: Translation2d, b: Translation2d): Double {
            return a.x * b.y - a.y * b.x
        }
    }

}
