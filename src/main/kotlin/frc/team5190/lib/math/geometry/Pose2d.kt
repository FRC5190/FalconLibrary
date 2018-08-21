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
import frc.team5190.lib.math.geometry.interfaces.IPose2d

// Stores a position on the field. Contains a translation and a rotation
class Pose2d : IPose2d<Pose2d> {
    // Translation and Rotation elements
    override val translation: Translation2d
    override val rotation: Rotation2d

    // Pose
    override val pose: Pose2d
        get() = this

    // Constructors
    constructor() {
        translation = Translation2d()
        rotation = Rotation2d()
    }

    constructor(x: Double, y: Double, rotation: Rotation2d) {
        translation = Translation2d(x, y)
        this.rotation = rotation
    }

    constructor(translation: Translation2d, rotation: Rotation2d) {
        this.translation = translation
        this.rotation = rotation
    }

    constructor(other: Pose2d) {
        translation = Translation2d(other.translation)
        rotation = Rotation2d(other.rotation)
    }


    override fun transformBy(transform: Pose2d): Pose2d {
        return Pose2d(translation.translateBy(transform.translation.rotateBy(rotation)),
                rotation.rotateBy(transform.rotation))
    }


    infix fun inFrameOfReferenceOf(fieldRelativeOrigin: Pose2d): Pose2d {
        return fieldRelativeOrigin.inverse.transformBy(this)
    }


    private val inverse: Pose2d
        get() {
            val rotationInverted = rotation.inverse
            return Pose2d(translation.inverse.rotateBy(rotationInverted), rotationInverted)
        }

    val normal: Pose2d
        get() {
            return Pose2d(translation, rotation.normal)
        }


    fun intersection(other: Pose2d): Translation2d {
        val otherRotation = other.rotation
        if (rotation.isParallel(otherRotation)) {
            return Translation2d(java.lang.Double.POSITIVE_INFINITY, java.lang.Double.POSITIVE_INFINITY)
        }
        return if (Math.abs(rotation.cos) < Math.abs(otherRotation.cos)) {
            intersectionInternal(this, other)
        } else {
            intersectionInternal(other, this)
        }
    }


    fun isCollinear(other: Pose2d): Boolean {
        if (!rotation.isParallel(other.rotation))
            return false
        val twist = toTwist(inverse.transformBy(other))
        return twist.dy epsilonEquals 0.00 && twist.dtheta epsilonEquals 0.0
    }

    private infix fun epsilonEquals(other: Pose2d): Boolean {
        return translation epsilonEquals other.translation && rotation.isParallel(other.rotation)
    }


    override fun interpolate(upperVal: Pose2d, interpolatePoint: Double): Pose2d {
        if (interpolatePoint <= 0) {
            return Pose2d(this)
        } else if (interpolatePoint >= 1) {
            return Pose2d(upperVal)
        }
        val twist = Pose2d.toTwist(inverse.transformBy(upperVal))
        return transformBy(Pose2d.fromTwist(twist.scaled(interpolatePoint)))
    }

    override fun toString(): String {
        return "T:" + translation.toString() + ", R:" + rotation.toString()
    }

    override fun toCSV(): String {
        return translation.toCSV() + "," + rotation.toCSV()
    }

    override fun distance(other: Pose2d): Double {
        return Pose2d.toTwist(inverse.transformBy(other)).norm()
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Pose2d) false else epsilonEquals(other)
    }

    override val mirror: Pose2d
        get() {
            return Pose2d(Translation2d(translation.x, 27 - translation.y), rotation.inverse)
        }

    companion object {
        private val kIdentity = Pose2d()

        fun identity(): Pose2d {
            return kIdentity
        }

        private const val kEpsilon = 1E-9

        fun fromTranslation(translation: Translation2d): Pose2d {
            return Pose2d(translation, Rotation2d())
        }

        fun fromRotation(rotation: Rotation2d): Pose2d {
            return Pose2d(Translation2d(), rotation)
        }


        fun fromTwist(delta: Twist2d): Pose2d {
            val sinTheta = Math.sin(delta.dtheta)
            val cosTheta = Math.cos(delta.dtheta)
            val s: Double
            val c: Double

            if (Math.abs(delta.dtheta) < kEpsilon) {
                s = 1.0 - 1.0 / 6.0 * delta.dtheta * delta.dtheta
                c = .5 * delta.dtheta
            } else {
                s = sinTheta / delta.dtheta
                c = (1.0 - cosTheta) / delta.dtheta
            }
            return Pose2d(Translation2d(delta.dx * s - delta.dy * c, delta.dx * c + delta.dy * s),
                    Rotation2d(cosTheta, sinTheta, false))
        }


        fun toTwist(transform: Pose2d): Twist2d {
            val dtheta = transform.rotation.radians
            val halfDTheta = 0.5 * dtheta
            val cosMinusOne = transform.rotation.cos - 1.0
            val halfThetaByTanOfHalfDTheta: Double

            halfThetaByTanOfHalfDTheta = if (Math.abs(cosMinusOne) < kEpsilon) {
                1.0 - 1.0 / 12.0 * dtheta * dtheta
            } else {
                -(halfDTheta * transform.rotation.sin) / cosMinusOne
            }

            val translationPart = transform.translation
                    .rotateBy(Rotation2d(halfThetaByTanOfHalfDTheta, -halfDTheta, false))
            return Twist2d(translationPart.x, translationPart.y, dtheta)
        }

        private fun intersectionInternal(a: Pose2d, b: Pose2d): Translation2d {
            val ar = a.rotation
            val br = b.rotation
            val at = a.translation
            val bt = b.translation

            val tanB = br.tan
            val t = ((at.x - bt.x) * tanB + bt.y - at.y) / (ar.sin - ar.cos * tanB)
            return if (java.lang.Double.isNaN(t)) {
                Translation2d(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY)
            } else at.translateBy(ar.toTranslation().scale(t))
        }
    }
}
