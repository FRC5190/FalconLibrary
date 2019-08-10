/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.log
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.meter
import kotlin.math.hypot
import kotlin.math.pow
import kotlin.math.sqrt

class QuinticHermiteSpline(
    private val x0: Double,
    private val x1: Double,
    private val dx0: Double,
    private val dx1: Double,
    private var ddx0: Double,
    private var ddx1: Double,
    private val y0: Double,
    private val y1: Double,
    private val dy0: Double,
    private val dy1: Double,
    private var ddy0: Double,
    private var ddy1: Double
) {

    constructor(start: Pose2d, end: Pose2d) : this(
        x0 = start.translation.x,
        x1 = end.translation.x,
        dx0 = 1.2 * start.translation.getDistance(end.translation) * start.rotation.cos,
        dx1 = 1.2 * start.translation.getDistance(end.translation) * end.rotation.cos,
        ddx0 = 0.0,
        ddx1 = 0.0,
        y0 = start.translation.y,
        y1 = end.translation.y,
        dy0 = 1.2 * start.translation.getDistance(end.translation) * start.rotation.sin,
        dy1 = 1.2 * start.translation.getDistance(end.translation) * end.rotation.sin,
        ddy0 = 0.0,
        ddy1 = 0.0
    )

    private var ax = 0.0
    private var bx = 0.0
    private var cx = 0.0
    private var dx = 0.0
    private var ex = 0.0
    private var fx = 0.0

    private var ay = 0.0
    private var by = 0.0
    private var cy = 0.0
    private var dy = 0.0
    private var ey = 0.0
    private var fy = 0.0

    val startPose get() = Pose2d(
        Translation2d(x0.meter, y0.meter),
        Rotation2d(dx0, dy0)
    )
    val endPose get() = Pose2d(
        Translation2d(x1.meter, y1.meter),
        Rotation2d(dx1, dy1)
    )

    init {
        computeCoefficients()
    }

    // Perform hermite matrix multiplication to compute polynomial coefficients
    @Suppress("DuplicatedCode")
    private fun computeCoefficients() {
        ax = -6 * x0 - 3 * dx0 - 0.5 * ddx0 + 0.5 * ddx1 - 3 * dx1 + 6 * x1
        bx = 15 * x0 + 8 * dx0 + 1.5 * ddx0 - ddx1 + 7 * dx1 - 15 * x1
        cx = -10 * x0 - 6 * dx0 - 1.5 * ddx0 + 0.5 * ddx1 - 4 * dx1 + 10 * x1
        dx = 0.5 * ddx0
        ex = dx0
        fx = x0

        ay = -6 * y0 - 3 * dy0 - 0.5 * ddy0 + 0.5 * ddy1 - 3 * dy1 + 6 * y1
        by = 15 * y0 + 8 * dy0 + 1.5 * ddy0 - ddy1 + 7 * dy1 - 15 * y1
        cy = -10 * y0 - 6 * dy0 - 1.5 * ddy0 + 0.5 * ddy1 - 4 * dy1 + 10 * y1
        dy = 0.5 * ddy0
        ey = dy0
        fy = y0
    }

    // Get point at a specified t
    fun getPoint(t: Double): Translation2d {
        val x = ax * t.pow(5) + bx * t.pow(4) + cx * t.pow(3) + dx * t.pow(2) + ex * t + fx
        val y = ay * t.pow(5) + by * t.pow(4) + cy * t.pow(3) + dy * t.pow(2) + ey * t + fy
        return Translation2d(x.meter, y.meter)
    }

    private fun dx(t: Double) =
        5.0 * ax * t.pow(4) + 4.0 * bx * t.pow(3) + 3.0 * cx * t.pow(2) + 2.0 * dx * t + ex

    private fun dy(t: Double) =
        5.0 * ay * t.pow(4) + 4.0 * by * t.pow(3) + 3.0 * cy * t.pow(2) + 2.0 * dy * t + ey

    private fun ddx(t: Double) =
        20.0 * ax * t.pow(3) + 12.0 * bx * t.pow(2) + 6.0 * cx * t + 2 * dx

    private fun ddy(t: Double) =
        20.0 * ay * t.pow(3) + 12.0 * by * t.pow(2) + 6.0 * cy * t + 2 * dy

    private fun dddx(t: Double) =
        60.0 * ax * t.pow(2) + 24.0 * bx * t + 6 * cx

    private fun dddy(t: Double) =
        60.0 * ay * t.pow(2) + 24.0 * by * t + 6 * cy

    fun getVelocity(t: Double) = hypot(dx(t), dy(t))

    fun getCurvature(t: Double) =
        (dx(t) * ddy(t) - ddx(t) * dy(t)) / ((dx(t) * dx(t) + dy(t) * dy(t)) * getVelocity(t))

    fun getDCurvature(t: Double): Double {
        val dx2dy2 = dx(t) * dx(t) + dy(t) * dy(t)
        val num =
            (dx(t) * dddy(t) - dddx(t) * dy(t)) * dx2dy2 - 3.0 * (dx(t) * ddy(t) - ddx(t) * dy(t)) * (dx(t) * ddx(t) + dy(
                t
            ) * ddy(t))
        return num / (dx2dy2 * dx2dy2 * sqrt(dx2dy2))
    }

    private fun dCurvature2(t: Double): Double {
        val dx2dy2 = dx(t) * dx(t) + dy(t) * dy(t)
        val num =
            (dx(t) * dddy(t) - dddx(t) * dy(t)) * dx2dy2 - 3.0 * (dx(t) * ddy(t) - ddx(t) * dy(t)) * (dx(t) * ddx(t) + dy(
                t
            ) * ddy(t))
        return num * num / (dx2dy2 * dx2dy2 * dx2dy2 * dx2dy2 * dx2dy2)
    }

    fun getHeading(t: Double) = Rotation2d(dx(t), dy(t))

    fun getPose2dWithCurvature(t: Double) =
        Pose2dWithCurvature(Pose2d(getPoint(t), getHeading(t)), getCurvature(t), getDCurvature(t) / getVelocity(t))

    companion object {
        private const val kMinSampleSize = 1

        @Suppress("LongParameterList")
        private fun parameterizeSpline(
            s: QuinticHermiteSpline,
            maxDx: SIUnit<Meter>,
            maxDy: SIUnit<Meter>,
            maxDTheta: SIUnit<Radian>,
            t0: Double = 0.0,
            t1: Double = 1.0
        ): List<Pose2dWithCurvature> {
            val dt = t1 - t0
            val rv = ArrayList<Pose2dWithCurvature>((kMinSampleSize / dt).toInt())
            rv.add(s.getPose2dWithCurvature(0.0))
            var t = 0.0
            while (t < t1) {
                val nextTime = t + dt / kMinSampleSize
                rv += getSegmentArc(
                    s,
                    t,
                    nextTime,
                    maxDx,
                    maxDy,
                    maxDTheta
                )
                t = nextTime
            }
            return rv
        }

        fun parameterizeSplines(
            splines: List<QuinticHermiteSpline>,
            maxDx: SIUnit<Meter>,
            maxDy: SIUnit<Meter>,
            maxDTheta: SIUnit<Radian>
        ): List<Pose2dWithCurvature> {
            if (splines.isEmpty()) return emptyList()
            val rv = ArrayList<Pose2dWithCurvature>(splines.size * kMinSampleSize + 1)
            rv.add(splines.first().getPose2dWithCurvature(0.0))
            for (s in splines) {
                val samples =
                    parameterizeSpline(
                        s,
                        maxDx,
                        maxDy,
                        maxDTheta
                    )
                rv.addAll(samples.subList(1, samples.size))
            }
            return rv
        }

        @Suppress("LongParameterList")
        private fun getSegmentArc(
            s: QuinticHermiteSpline,
            t0: Double,
            t1: Double,
            maxDx: SIUnit<Meter>,
            maxDy: SIUnit<Meter>,
            maxDTheta: SIUnit<Radian>
        ): Array<Pose2dWithCurvature> {
            val p0 = s.getPoint(t0)
            val p1 = s.getPoint(t1)
            val r0 = s.getHeading(t0)
            val r1 = s.getHeading(t1)
            val transformation = Pose2d((p1 - p0).rotateBy(-r0), r1 + -r0)

            val twist = transformation.log()

            return if (twist.dy > maxDy.value || twist.dx > maxDx.value || twist.dtheta > maxDTheta.value) {
                getSegmentArc(
                    s,
                    t0,
                    (t0 + t1) / 2,
                    maxDx,
                    maxDy,
                    maxDTheta
                ) +
                    getSegmentArc(
                        s,
                        (t0 + t1) / 2,
                        t1,
                        maxDx,
                        maxDy,
                        maxDTheta
                    )
            } else {
                arrayOf(s.getPose2dWithCurvature(t1))
            }
        }
    }
}