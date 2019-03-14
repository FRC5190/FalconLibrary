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
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import kotlin.math.pow

class ParametricQuinticHermiteSpline(
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
) : ParametricSpline() {

    constructor(start: Pose2d, end: Pose2d) : this(
        x0 = start.translation.x,
        x1 = end.translation.x,
        dx0 = 1.2 * start.translation.distance(end.translation) * start.rotation.cos,
        dx1 = 1.2 * start.translation.distance(end.translation) * end.rotation.cos,
        ddx0 = 0.0,
        ddx1 = 0.0,
        y0 = start.translation.y,
        y1 = end.translation.y,
        dy0 = 1.2 * start.translation.distance(end.translation) * start.rotation.sin,
        dy1 = 1.2 * start.translation.distance(end.translation) * end.rotation.sin,
        ddy0 = 0.0,
        ddy1 = 0.0
    )

    /*
    private var xCoefficients = mat[0.0, 0.0, 0.0, 0.0, 0.0, 0.0].T
    private var yCoefficients = mat[0.0, 0.0, 0.0, 0.0, 0.0, 0.0].T
    */

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

    val startPose get() = Pose2d(Translation2d(x0, y0), Rotation2d(dx0, dy0, true))
    val endPose get() = Pose2d(Translation2d(x1, y1), Rotation2d(dx1, dy1, true))

    init {
        computeCoefficients()
    }

    // Perform hermite matrix multiplication to compute polynomial coefficients
    private fun computeCoefficients() {
        /*
        val hermite = mat[
                -06.0, -03.0, -00.5, +00.5, -03.0, +06.0 end
                +15.0, +08.0, +01.5, -01.0, +07.0, -15.0 end
                -10.0, -06.0, -01.5, +00.5, -04.0, +10.0 end
                +00.0, +00.0, +00.5, +00.0, +00.0, +00.0 end
                +00.0, +01.0, +00.0, +00.0, +00.0, +00.0 end
                +01.0, +00.0, +00.0, +00.0, +00.0, +00.0]

        val x = mat[x0, dx0, ddx0, ddx1, dx1, x1].T
        val y = mat[y0, dy0, ddy0, ddy1, dy1, y1].T

        xCoefficients = hermite * x
        yCoefficients = hermite * y
        */

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
    override fun getPoint(t: Double): Translation2d {
        val x = ax * t.pow(5) + bx * t.pow(4) + cx * t.pow(3) + dx * t.pow(2) + ex * t + fx
        val y = ay * t.pow(5) + by * t.pow(4) + cy * t.pow(3) + dy * t.pow(2) + ey * t + fy
        return Translation2d(x, y)
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

    override fun getVelocity(t: Double) = Math.hypot(dx(t), dy(t))

    override fun getCurvature(t: Double) =
        (dx(t) * ddy(t) - ddx(t) * dy(t)) / ((dx(t) * dx(t) + dy(t) * dy(t)) * getVelocity(t))

    override fun getDCurvature(t: Double): Double {
        val dx2dy2 = dx(t) * dx(t) + dy(t) * dy(t)
        val num =
            (dx(t) * dddy(t) - dddx(t) * dy(t)) * dx2dy2 - 3.0 * (dx(t) * ddy(t) - ddx(t) * dy(t)) * (dx(t) * ddx(t) + dy(
                t
            ) * ddy(t))
        return num / (dx2dy2 * dx2dy2 * Math.sqrt(dx2dy2))
    }

    private fun dCurvature2(t: Double): Double {
        val dx2dy2 = dx(t) * dx(t) + dy(t) * dy(t)
        val num =
            (dx(t) * dddy(t) - dddx(t) * dy(t)) * dx2dy2 - 3.0 * (dx(t) * ddy(t) - ddx(t) * dy(t)) * (dx(t) * ddx(t) + dy(
                t
            ) * ddy(t))
        return num * num / (dx2dy2 * dx2dy2 * dx2dy2 * dx2dy2 * dx2dy2)
    }

    override fun getHeading(t: Double) = Rotation2d(dx(t), dy(t), true)

    private fun sumDCurvature2(): Double {
        val dt = 1.0 / kSamples
        var sum = 0.0
        var t = 0.0
        while (t < 1.0) {
            sum += dt * dCurvature2(t)
            t += dt
        }
        return sum
    }

    companion object {
        private const val kEpsilon = 1e-5
        private const val kStepSize = 1.0
        private const val kMinDelta = 0.001
        private const val kSamples = 100
        private const val kMaxIterations = 100

        private class ControlPoint {
            var ddx: Double = 0.toDouble()
            var ddy: Double = 0.toDouble()
        }

        private fun sumDCurvature2(splines: List<ParametricQuinticHermiteSpline>): Double {
            var sum = 0.0
            for (s in splines) {
                sum += s.sumDCurvature2()
            }
            return sum
        }

        // Run an optimization algorithm to reduce curvature along the spline
        fun optimizeSpline(splines: MutableList<ParametricQuinticHermiteSpline>): Double {
            var count = 0
            var prev = sumDCurvature2(splines)
            while (count < kMaxIterations) {
                runOptimizationIteration(splines)
                val current = sumDCurvature2(splines)
                if (prev - current < kMinDelta) return current
                prev = current
                count++
            }
            return prev
        }

        @Suppress("ComplexMethod")
        private fun runOptimizationIteration(splines: MutableList<ParametricQuinticHermiteSpline>) {
            //can't optimize anything with less than 2 splines
            if (splines.size <= 1) {
                return
            }

            val controlPoints = ArrayList<ControlPoint>(splines.size - 1)

            for (i in 0 until splines.size - 1) {
                controlPoints.add(ControlPoint())
            }

            var magnitude = 0.0

            for (i in 0 until splines.size - 1) {
                //don't try to optimize colinear points
                if (splines[i].startPose.isCollinear(splines[i + 1].startPose) || splines[i].endPose.isCollinear(splines[i + 1].endPose)) {
                    continue
                }
                val original = sumDCurvature2(splines)
                val temp: ParametricQuinticHermiteSpline = splines[i]
                val temp1: ParametricQuinticHermiteSpline = splines[i + 1]

                controlPoints[i] = ControlPoint() //holds the gradient at a control point

                //calculate partial derivatives of sumDCurvature2
                splines[i] = ParametricQuinticHermiteSpline(
                    temp.x0,
                    temp.x1,
                    temp.dx0,
                    temp.dx1,
                    temp.ddx0,
                    temp.ddx1 + kEpsilon,
                    temp.y0,
                    temp.y1,
                    temp.dy0,
                    temp.dy1,
                    temp.ddy0,
                    temp.ddy1
                )
                splines[i + 1] = ParametricQuinticHermiteSpline(
                    temp1.x0,
                    temp1.x1,
                    temp1.dx0,
                    temp1.dx1,
                    temp1.ddx0 + kEpsilon,
                    temp1.ddx1,
                    temp1.y0,
                    temp1.y1,
                    temp1.dy0,
                    temp1.dy1,
                    temp1.ddy0,
                    temp1.ddy1
                )
                controlPoints[i].ddx = (sumDCurvature2(splines) - original) / kEpsilon
                splines[i] = ParametricQuinticHermiteSpline(
                    temp.x0, temp.x1, temp.dx0, temp.dx1, temp.ddx0, temp.ddx1, temp
                        .y0, temp.y1, temp.dy0, temp.dy1, temp.ddy0, temp.ddy1 + kEpsilon
                )
                splines[i + 1] = ParametricQuinticHermiteSpline(
                    temp1.x0, temp1.x1, temp1.dx0, temp1.dx1, temp1.ddx0,
                    temp1.ddx1, temp1.y0, temp1.y1, temp1.dy0, temp1.dy1, temp1.ddy0 + kEpsilon, temp1.ddy1
                )
                controlPoints[i].ddy = (sumDCurvature2(splines) - original) / kEpsilon

                splines[i] = temp
                splines[i + 1] = temp1
                magnitude += controlPoints[i].ddx * controlPoints[i].ddx + controlPoints[i].ddy * controlPoints[i].ddy
            }

            magnitude = Math.sqrt(magnitude)

            //minimize along the direction of the gradient
            //first calculate 3 points along the direction of the gradient
            val p2 = Translation2d(0.0, sumDCurvature2(splines)) //middle point is at the current location

            for (i in 0 until splines.size - 1) { //first point is offset from the middle location by -stepSize
                if (splines[i].startPose.isCollinear(splines[i + 1].startPose) || splines[i].endPose.isCollinear(splines[i + 1].endPose)) {
                    continue
                }
                //normalize to step size
                controlPoints[i].ddx *= kStepSize / magnitude
                controlPoints[i].ddy *= kStepSize / magnitude

                //move opposite the gradient by step size amount
                splines[i].ddx1 -= controlPoints[i].ddx
                splines[i].ddy1 -= controlPoints[i].ddy
                splines[i + 1].ddx0 -= controlPoints[i].ddx
                splines[i + 1].ddy0 -= controlPoints[i].ddy

                //recompute the spline's coefficients to account for new second derivatives
                splines[i].computeCoefficients()
                splines[i + 1].computeCoefficients()
            }
            val p1 = Translation2d(-kStepSize, sumDCurvature2(splines))

            for (i in 0 until splines.size - 1) { //last point is offset from the middle location by +stepSize
                if (splines[i].startPose.isCollinear(splines[i + 1].startPose) || splines[i].endPose.isCollinear(splines[i + 1].endPose)) {
                    continue
                }
                //move along the gradient by 2 times the step size amount (to return to original location and move by 1
                // step)
                splines[i].ddx1 += 2 * controlPoints[i].ddx
                splines[i].ddy1 += 2 * controlPoints[i].ddy
                splines[i + 1].ddx0 += 2 * controlPoints[i].ddx
                splines[i + 1].ddy0 += 2 * controlPoints[i].ddy

                //recompute the spline's coefficients to account for new second derivatives
                splines[i].computeCoefficients()
                splines[i + 1].computeCoefficients()
            }

            val p3 = Translation2d(kStepSize, sumDCurvature2(splines))

            val stepSize = FunctionalQuadraticSpline(
                p1,
                p2,
                p3
            ).vertexXCoordinate //approximate step size to minimize sumDCurvature2 along the gradient

            for (i in 0 until splines.size - 1) {
                if (splines[i].startPose.isCollinear(splines[i + 1].startPose) || splines[i].endPose.isCollinear(splines[i + 1].endPose)) {
                    continue
                }
                //move by the step size calculated by the parabola fit (+1 to offset for the final transformation to find
                // p3)
                controlPoints[i].ddx *= 1 + stepSize / kStepSize
                controlPoints[i].ddy *= 1 + stepSize / kStepSize

                splines[i].ddx1 += controlPoints[i].ddx
                splines[i].ddy1 += controlPoints[i].ddy
                splines[i + 1].ddx0 += controlPoints[i].ddx
                splines[i + 1].ddy0 += controlPoints[i].ddy

                //recompute the spline's coefficients to account for new second derivatives
                splines[i].computeCoefficients()
                splines[i + 1].computeCoefficients()
            }
        }
    }
}