/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.spline

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Translation2d
import java.util.*

object SplineGenerator {
    private const val kMaxDX = 2.0
    private const val kMaxDY = 0.05
    private const val kMaxDTheta = 0.1
    private const val kMinSampleSize = 1


    private fun parameterizeSpline(s: Spline, maxDx: Double = kMaxDX, maxDy: Double = kMaxDY, maxDTheta: Double = kMaxDTheta,
                                   t0: Double = 0.0, t1: Double = 1.0): ArrayList<Pose2dWithCurvature> {

        val rv = ArrayList<Pose2dWithCurvature>()
        rv.add(s.getPose2dWithCurvature(0.0))
        val dt = t1 - t0
        var t = 0.0
        while (t < t1) {
            getSegmentArc(s, rv, t, t + dt / kMinSampleSize, maxDx, maxDy, maxDTheta)
            t += dt / kMinSampleSize
        }
        return rv
    }

    fun parameterizeSplines(splines: List<Spline>, maxDx: Double, maxDy: Double,
                            maxDTheta: Double): List<Pose2dWithCurvature> {
        val rv = ArrayList<Pose2dWithCurvature>()
        if (splines.isEmpty()) return rv
        rv.add(splines[0].getPose2dWithCurvature(0.0))
        for (s in splines) {
            val samples = parameterizeSpline(s, maxDx, maxDy, maxDTheta)
            samples.removeAt(0)
            rv.addAll(samples)
        }
        return rv
    }

    private fun getSegmentArc(s: Spline, rv: MutableList<Pose2dWithCurvature>, t0: Double, t1: Double, maxDx: Double,
                              maxDy: Double,
                              maxDTheta: Double) {
        val p0 = s.getPoint(t0)
        val p1 = s.getPoint(t1)
        val r0 = s.getHeading(t0)
        val r1 = s.getHeading(t1)
        val transformation = Pose2d(Translation2d(p0, p1).rotateBy(r0.inverse), r1.rotateBy(r0.inverse))
        val twist = Pose2d.toTwist(transformation)
        if (twist.dy > maxDy || twist.dx > maxDx || twist.dtheta > maxDTheta) {
            getSegmentArc(s, rv, t0, (t0 + t1) / 2, maxDx, maxDy, maxDTheta)
            getSegmentArc(s, rv, (t0 + t1) / 2, t1, maxDx, maxDy, maxDTheta)
        } else {
            rv.add(s.getPose2dWithCurvature(t1))
        }
    }

}
