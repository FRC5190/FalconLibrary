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
import org.ghrobotics.lib.mathematics.units.Rotation2d
import java.util.*

object ParametricSplineGenerator {
    private const val kMinSampleSize = 1

    @Suppress("LongParameterList")
    private fun parameterizeSpline(
        s: ParametricSpline,
        maxDx: Double,
        maxDy: Double,
        maxDTheta: Rotation2d,
        t0: Double = 0.0,
        t1: Double = 1.0
    ): List<Pose2dWithCurvature> {
        val dt = t1 - t0
        val rv = ArrayList<Pose2dWithCurvature>((kMinSampleSize / dt).toInt())
        rv.add(s.getPose2dWithCurvature(0.0))
        var t = 0.0
        while (t < t1) {
            val nextTime = t + dt / kMinSampleSize
            rv += getSegmentArc(s, t, nextTime, maxDx, maxDy, maxDTheta)
            t = nextTime
        }
        return rv
    }

    fun parameterizeSplines(
        splines: List<ParametricSpline>,
        maxDx: Double,
        maxDy: Double,
        maxDTheta: Rotation2d
    ): List<Pose2dWithCurvature> {
        if (splines.isEmpty()) return emptyList()
        val rv = ArrayList<Pose2dWithCurvature>(splines.size * kMinSampleSize + 1)
        rv.add(splines.first().getPose2dWithCurvature(0.0))
        for (s in splines) {
            val samples = parameterizeSpline(s, maxDx, maxDy, maxDTheta)
            rv.addAll(samples.subList(1, samples.size))
        }
        return rv
    }

    @Suppress("LongParameterList")
    private fun getSegmentArc(
        s: ParametricSpline,
        t0: Double,
        t1: Double,
        maxDx: Double,
        maxDy: Double,
        maxDTheta: Rotation2d
    ): Array<Pose2dWithCurvature> {
        val p0 = s.getPoint(t0)
        val p1 = s.getPoint(t1)
        val r0 = s.getHeading(t0)
        val r1 = s.getHeading(t1)
        val transformation = Pose2d((p1 - p0) * -r0, r1 + -r0)
        val twist = transformation.twist
        return if (twist.dy > maxDy || twist.dx > maxDx || twist.dTheta > maxDTheta) {
            getSegmentArc(s, t0, (t0 + t1) / 2, maxDx, maxDy, maxDTheta) +
                getSegmentArc(s, (t0 + t1) / 2, t1, maxDx, maxDy, maxDTheta)
        } else {
            arrayOf(s.getPose2dWithCurvature(t1))
        }
    }
}
