/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
/* ktlint-disable no-wildcard-imports */
import java.util.*

object ParametricSplineGenerator {
    private const val kMinSampleSize = 1

    private fun parameterizeSpline(
        s: ParametricSpline,
        maxDx: Length,
        maxDy: Length,
        maxDTheta: Rotation2d,
        t0: Double = 0.0,
        t1: Double = 1.0
    ): ArrayList<Pose2dWithCurvature> {

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

    fun parameterizeSplines(
        splines: List<ParametricSpline>,
        maxDx: Length,
        maxDy: Length,
        maxDTheta: Rotation2d
    ): List<Pose2dWithCurvature> {
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

    private fun getSegmentArc(
        s: ParametricSpline,
        rv: MutableList<Pose2dWithCurvature>,
        t0: Double,
        t1: Double,
        maxDx: Length,
        maxDy: Length,
        maxDTheta: Rotation2d
    ) {
        val p0 = s.getPoint(t0)
        val p1 = s.getPoint(t1)
        val r0 = s.getHeading(t0)
        val r1 = s.getHeading(t1)
        val transformation = Pose2d((p1 - p0) * -r0, r1 + -r0)
        val twist = transformation.twist
        if (twist.dy > maxDy || twist.dx > maxDx || twist.dTheta > maxDTheta) {
            getSegmentArc(s, rv, t0, (t0 + t1) / 2, maxDx, maxDy, maxDTheta)
            getSegmentArc(s, rv, (t0 + t1) / 2, t1, maxDx, maxDy, maxDTheta)
        } else {
            rv.add(s.getPose2dWithCurvature(t1))
        }
    }
}
