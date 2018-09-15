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
import org.ghrobotics.lib.mathematics.twodim.geometry.interfaces.ICurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.interfaces.IPose2d
import org.ghrobotics.lib.types.Interpolable

import java.text.DecimalFormat

class Pose2dWithCurvature(override val pose: Pose2d, override val curvature: Double, override val dkds: Double)
    : IPose2d<Pose2dWithCurvature>, ICurvature<Pose2dWithCurvature> {


    override val translation: Translation2d
        get() = pose.translation

    override val rotation: Rotation2d
        get() = pose.rotation

    constructor() : this(Pose2d(), 0.0, 0.0)
    constructor(pose: Pose2d, curvature: Double) : this(pose, curvature, 0.0)
    constructor(translation: Translation2d, rotation: Rotation2d, curvature: Double) : this(Pose2d(translation, rotation), curvature)
    constructor(translation: Translation2d, rotation: Rotation2d, curvature: Double, dcurvature_ds: Double) : this(Pose2d(translation, rotation), curvature, dcurvature_ds)

    override fun transformBy(transform: Pose2d): Pose2dWithCurvature {
        return Pose2dWithCurvature(pose.transformBy(transform), curvature, dkds)
    }

    override val mirror: Pose2dWithCurvature
        get() {
            return Pose2dWithCurvature(pose.mirror.pose, -curvature, -dkds)
        }

    override fun interpolate(upperVal: Pose2dWithCurvature, interpolatePoint: Double): Pose2dWithCurvature {
        return Pose2dWithCurvature(pose.interpolate(upperVal.pose, interpolatePoint),
                Interpolable.interpolate(curvature, upperVal.curvature, interpolatePoint),
                Interpolable.interpolate(dkds, upperVal.dkds, interpolatePoint))
    }

    override fun distance(other: Pose2dWithCurvature): Double {
        return pose.distance(other.pose)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is Pose2dWithCurvature) return false
        val p2dwc = other as Pose2dWithCurvature?
        return pose == p2dwc!!.pose && curvature epsilonEquals p2dwc.curvature && dkds epsilonEquals p2dwc.dkds
    }

    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return pose.toString() + ", curvature: " + fmt.format(curvature) + ", dcurvature_ds: " + fmt.format(dkds)
    }

    override fun toCSV(): String {
        val fmt = DecimalFormat("#0.000")
        return pose.toCSV() + "," + fmt.format(curvature) + "," + fmt.format(dkds)
    }

    companion object {
        private val kIdentity = Pose2dWithCurvature()

        fun identity(): Pose2dWithCurvature {
            return kIdentity
        }
    }
}
