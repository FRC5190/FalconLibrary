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
import frc.team5190.lib.math.geometry.interfaces.ICurvature
import frc.team5190.lib.math.geometry.interfaces.IPose2d
import frc.team5190.lib.types.Interpolable

import java.text.DecimalFormat

class Pose2dWithCurvature : IPose2d<Pose2dWithCurvature>, ICurvature<Pose2dWithCurvature> {

    override val pose: Pose2d
    override val curvature: Double
    override val dkds: Double

    override val translation: Translation2d
        get() = pose.translation

    override val rotation: Rotation2d
        get() = pose.rotation

    constructor() {
        pose = Pose2d()
        curvature = 0.0
        dkds = 0.0
    }

    constructor(pose: Pose2d, curvature: Double) {
        this.pose = pose
        this.curvature = curvature
        dkds = 0.0
    }

    constructor(pose: Pose2d, curvature: Double, dcurvature_ds: Double) {
        this.pose = pose
        this.curvature = curvature
        dkds = dcurvature_ds
    }

    constructor(translation: Translation2d, rotation: Rotation2d, curvature: Double) {
        pose = Pose2d(translation, rotation)
        this.curvature = curvature
        dkds = 0.0
    }

    constructor(translation: Translation2d, rotation: Rotation2d, curvature: Double, dcurvature_ds: Double) {
        pose = Pose2d(translation, rotation)
        this.curvature = curvature
        dkds = dcurvature_ds
    }

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
