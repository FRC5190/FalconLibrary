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
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.types.VaryInterpolatable
import kotlin.math.absoluteValue

// Stores a pose on the field. Contains a translation and a rotation

data class Pose2d(
    val translation: Translation2d = Translation2d(),
    val rotation: Rotation2d = 0.degree
) : VaryInterpolatable<Pose2d> {

    constructor(
        x: Length,
        y: Length,
        rotation: Rotation2d = 0.degree
    ) : this(Translation2d(x, y), rotation)

    val twist: Twist2d
        get() {
            val dtheta = rotation.radian
            val halfDTheta = dtheta / 2.0
            val cosMinusOne = rotation.cos - 1.0

            val halfThetaByTanOfHalfDTheta = if (cosMinusOne.absoluteValue < kEpsilon) {
                1.0 - 1.0 / 12.0 * dtheta * dtheta
            } else {
                -(halfDTheta * rotation.sin) / cosMinusOne
            }
            val translationPart = translation *
                Rotation2d(halfThetaByTanOfHalfDTheta, -halfDTheta, false)
            return Twist2d(translationPart.x, translationPart.y, rotation)
        }

    val mirror get() = Pose2d(Translation2d(translation.x, 27.feet.value - translation.y), -rotation)

    infix fun inFrameOfReferenceOf(fieldRelativeOrigin: Pose2d) = (-fieldRelativeOrigin) + this

    operator fun plus(other: Pose2d) = transformBy(other)

    operator fun minus(other: Pose2d) = this + -other

    fun transformBy(other: Pose2d) =
        Pose2d(
            translation + (other.translation * rotation),
            rotation + other.rotation
        )

    operator fun unaryMinus(): Pose2d {
        val invertedRotation = -rotation
        return Pose2d((-translation) * invertedRotation, invertedRotation)
    }

    fun isCollinear(other: Pose2d): Boolean {
        if (!rotation.isParallel(other.rotation)) return false
        val twist = (-this + other).twist
        return twist.dy epsilonEquals 0.0 && twist.dTheta.value epsilonEquals 0.0
    }

    override fun interpolate(endValue: Pose2d, t: Double): Pose2d {
        if (t <= 0) {
            return Pose2d(this.translation, this.rotation)
        } else if (t >= 1) {
            return Pose2d(endValue.translation, endValue.rotation)
        }
        val twist = (-this + endValue).twist
        return this + (twist * t).asPose
    }

    override fun distance(other: Pose2d) = (-this + other).twist.norm
}