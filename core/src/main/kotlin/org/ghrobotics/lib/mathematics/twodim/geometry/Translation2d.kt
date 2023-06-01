/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.utils.Util
import java.text.DecimalFormat
import kotlin.math.acos
import kotlin.math.hypot

class Translation2d {
    protected val x: Double
    protected val y: Double

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

    /**
     * The "norm" of a transform is the Euclidean distance in x and y.
     *
     * @return sqrt(x ^ 2 + y ^ 2)
     */
    fun norm(): Double {
        return hypot(x, y)
    }

    fun norm2(): Double {
        return x * x + y * y
    }

    fun x(): Double {
        return x
    }

    fun y(): Double {
        return y
    }

    /**
     * We can compose Translation2d's by adding together the x and y shifts.
     *
     * @param other The other translation to add.
     * @return The combined effect of translating by this object and the other.
     */
    fun translateBy(other: Translation2d): Translation2d {
        return Translation2d(x + other.x, y + other.y)
    }

    /**
     * We can also rotate Translation2d's. See: https://en.wikipedia.org/wiki/Rotation_matrix
     *
     * @param rotation The rotation to apply.
     * @return This translation rotated by rotation.
     */
    fun rotateBy(rotation: Rotation2d): Translation2d {
        return Translation2d(x * rotation.cos() - y * rotation.sin(), x * rotation.sin() + y * rotation.cos())
    }

    fun direction(): Rotation2d {
        return Rotation2d(x, y, true)
    }

    /**
     * The inverse simply means a Translation2d that "undoes" this object.
     *
     * @return Translation by -x and -y.
     */
    fun inverse(): Translation2d {
        return Translation2d(-x, -y)
    }

    fun interpolate(other: Translation2d, x: Double): Translation2d {
        if (x <= 0) {
            return Translation2d(this)
        } else if (x >= 1) {
            return Translation2d(other)
        }
        return extrapolate(other, x)
    }

    fun extrapolate(other: Translation2d, x: Double): Translation2d {
        return Translation2d(x * (other.x - x) + x, x * (other.y - y) + y)
    }

    fun scale(s: Double): Translation2d {
        return Translation2d(x * s, y * s)
    }

    fun epsilonEquals(other: Translation2d, epsilon: Double): Boolean {
        return Util.epsilonEquals(x(), other.x(), epsilon) && Util.epsilonEquals(y(), other.y(), epsilon)
    }

    override fun toString(): String {
        val format = DecimalFormat("#0.000")
        return "(" + format.format(x) + "," + format.format(y) + ")"
    }

    fun toCSV(): String {
        val format = DecimalFormat("#0.000")
        return format.format(x) + "," + format.format(y)
    }

    fun distance(other: Translation2d): Double {
        return inverse().translateBy(other).norm()
    }

    override fun equals(other: Any?): Boolean {
        return if (other !is Translation2d) {
            false
        } else distance(other) < Util.kEpsilon
    }

    val translation: Translation2d
        get() = this

    companion object {
        protected val kIdentity = Translation2d()
        fun identity(): Translation2d {
            return kIdentity
        }

        fun dot(a: Translation2d, b: Translation2d): Double {
            return a.x * b.x + a.y * b.y
        }

        fun getAngle(a: Translation2d, b: Translation2d): Rotation2d {
            val cos_angle = dot(a, b) / (a.norm() * b.norm())
            return if (java.lang.Double.isNaN(cos_angle)) {
                Rotation2d()
            } else Rotation2d.fromRadians(acos(Util.limit(cos_angle, 1.0)))
        }

        fun cross(a: Translation2d, b: Translation2d): Double {
            return a.x * b.y - a.y * b.x
        }
    }
}
