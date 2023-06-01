/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.utils.Util
import java.text.DecimalFormat
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot
import kotlin.math.sign

open class Rotation2d {
    protected var cosAngle = Double.NaN
    protected var sinAngle = Double.NaN
    protected var radians_ = Double.NaN

    protected constructor(x: Double, y: Double, radians: Double) {
        cosAngle = x
        sinAngle = y
        radians_ = radians
    }

    constructor() : this(1.0, 0.0, 0.0) {}
    constructor(radians: Double, normalize: Boolean) {
        var radians = radians
        if (normalize) {
            radians = WrapRadians(radians)
        }
        radians_ = radians
    }

    constructor(x: Double, y: Double, normalize: Boolean) {
        if (normalize) {
            // From trig, we know that sin^2 + cos^2 == 1, but as we do math on this object
            // we might accumulate rounding errors.
            // Normalizing forces us to re-scale the sin and cos to reset rounding errors.
            val magnitude = hypot(x, y)
            if (magnitude > kEpsilon) {
                sinAngle = y / magnitude
                cosAngle = x / magnitude
            } else {
                sinAngle = 0.0
                cosAngle = 1.0
            }
        } else {
            cosAngle = x
            sinAngle = y
        }
    }

    constructor(other: Rotation2d) {
        cosAngle = other.cosAngle
        sinAngle = other.sinAngle
        radians_ = other.radians_
    }

    constructor(direction: Translation2d, normalize: Boolean) : this(direction.x(), direction.y(), normalize) {}

    fun cos(): Double {
        ensureTrigComputed()
        return cosAngle
    }

    fun sin(): Double {
        ensureTrigComputed()
        return sinAngle
    }

    fun tan(): Double {
        ensureTrigComputed()
        return if (Math.abs(cosAngle) < kEpsilon) {
            if (sinAngle >= 0.0) {
                Double.POSITIVE_INFINITY
            } else {
                Double.NEGATIVE_INFINITY
            }
        } else sinAngle / cosAngle
    }

    val radians: Double
        get() {
            ensureRadiansComputed()
            return radians_
        }
    val degrees: Double
        get() = Math.toDegrees(radians)

    /**
     * Based on Team 1323's method of the same name.
     *
     * @return Rotation2d representing the angle of the nearest axis to the angle in standard position
     */
    fun nearestPole(): Rotation2d {
        var pole_sin = 0.0
        var pole_cos = 0.0
        if (abs(cosAngle) > abs(sinAngle)) {
            pole_cos = sign(cosAngle)
            pole_sin = 0.0
        } else {
            pole_cos = 0.0
            pole_sin = sign(sinAngle)
        }
        return Rotation2d(pole_cos, pole_sin, false)
    }

    /**
     * We can rotate this Rotation2d by adding together the effects of it and
     * another rotation.
     *
     * @param other The other rotation. See:
     * https://en.wikipedia.org/wiki/Rotation_matrix
     * @return This rotation rotated by other.
     */
    fun rotateBy(other: Rotation2d): Rotation2d {
        return if (hasTrig() && other.hasTrig()) {
            Rotation2d(
                cosAngle * other.cosAngle - sinAngle * other.sinAngle,
                cosAngle * other.sinAngle + sinAngle * other.cosAngle,
                true,
            )
        } else {
            fromRadians(radians + other.radians)
        }
    }

    fun normal(): Rotation2d {
        return if (hasTrig()) {
            Rotation2d(-sinAngle, cosAngle, false)
        } else {
            fromRadians(radians - Math.PI / 2.0)
        }
    }

    /**
     * The inverse of a Rotation2d "undoes" the effect of this rotation.
     *
     * @return The opposite of this rotation.
     */
    fun inverse(): Rotation2d {
        return if (hasTrig()) {
            Rotation2d(cosAngle, -sinAngle, false)
        } else {
            fromRadians(-radians)
        }
    }

    fun isParallel(other: Rotation2d): Boolean {
        return if (hasRadians() && other.hasRadians()) {
            (
                Util.epsilonEquals(radians_, other.radians_) ||
                    Util.epsilonEquals(radians_, WrapRadians(other.radians_ + Math.PI))
                )
        } else if (hasTrig() && other.hasTrig()) {
            Util.epsilonEquals(sinAngle, other.sinAngle) && Util.epsilonEquals(cosAngle, other.cosAngle)
        } else {
            // Use public, checked version.
            (
                Util.epsilonEquals(radians, other.radians) ||
                    Util.epsilonEquals(radians_, WrapRadians(other.radians_ + Math.PI))
                )
        }
    }

    fun toTranslation(): Translation2d {
        ensureTrigComputed()
        return Translation2d(cosAngle, sinAngle)
    }

    protected fun WrapRadians(radians: Double): Double {
        var radians = radians
        val k2Pi = 2.0 * Math.PI
        radians %= k2Pi
        radians = (radians + k2Pi) % k2Pi
        if (radians > Math.PI) radians -= k2Pi
        return radians
    }

    private fun hasTrig(): Boolean {
        return !java.lang.Double.isNaN(sinAngle) && !java.lang.Double.isNaN(cosAngle)
    }

    private fun hasRadians(): Boolean {
        return !java.lang.Double.isNaN(radians_)
    }

    private fun ensureTrigComputed() {
        if (!hasTrig()) {
            if (java.lang.Double.isNaN(radians_)) {
                System.err.println("HEY")
            }
            sinAngle = kotlin.math.sin(radians_)
            cosAngle = kotlin.math.cos(radians_)
        }
    }

    private fun ensureRadiansComputed() {
        if (!hasRadians()) {
            if (java.lang.Double.isNaN(cosAngle) || java.lang.Double.isNaN(sinAngle)) {
                System.err.println("HEY")
            }
            radians_ = atan2(sinAngle, cosAngle)
        }
    }

    fun interpolate(other: Rotation2d, x: Double): Rotation2d {
        if (x <= 0.0) {
            return Rotation2d(this)
        } else if (x >= 1.0) {
            return Rotation2d(other)
        }
        val angle_diff = inverse().rotateBy(other).radians
        return rotateBy(fromRadians(angle_diff * x))
    }

    override fun toString(): String {
        return "(" + DecimalFormat("#0.000").format(degrees) + " deg)"
    }

    fun toCSV(): String {
        return DecimalFormat("#0.000").format(degrees)
    }

    fun distance(other: Rotation2d): Double {
        return inverse().rotateBy(other).radians
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Rotation2d) {
            false
        } else distance(other) < kEpsilon
    }

    val rotation: Rotation2d
        get() = this

    companion object {
        protected val kIdentity = Rotation2d()
        fun identity(): Rotation2d {
            return kIdentity
        }

        fun fromRadians(angle_radians: Double): Rotation2d {
            return Rotation2d(angle_radians, true)
        }

        fun fromDegrees(angle_degrees: Double): Rotation2d {
            return fromRadians(Math.toRadians(angle_degrees))
        }
    }
}
