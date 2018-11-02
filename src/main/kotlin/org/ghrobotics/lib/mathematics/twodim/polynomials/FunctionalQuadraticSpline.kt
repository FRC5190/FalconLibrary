package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

class FunctionalQuadraticSpline(
    private val p1: Translation2d,
    private val p2: Translation2d,
    private val p3: Translation2d
) {
    private val a get() = p3.xRaw * (p2.yRaw - p1.yRaw) + p2.xRaw * (p1.yRaw - p3.yRaw) + p1.xRaw * (p3.yRaw - p2.yRaw)
    private val b get() = p3.xRaw * p3.xRaw * (p1.yRaw - p2.yRaw) + p2.xRaw * p2.xRaw * (p3.yRaw - p1.yRaw) + p1.xRaw * p1.xRaw * (p2.yRaw - p3.yRaw)

    val vertexXCoordinate get() = -b / (2 * a)
}