package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

class FunctionalQuadraticSpline(
    private val p1: Translation2d,
    private val p2: Translation2d,
    private val p3: Translation2d
) {
    private val a get() = p3.x.value * (p2.y.value - p1.y.value) + p2.x.value * (p1.y.value - p3.y.value) + p1.x.value * (p3.y.value - p2.y.value)
    private val b get() = p3.x.value * p3.x.value * (p1.y.value - p2.y.value) + p2.x.value * p2.x.value * (p3.y.value - p1.y.value) + p1.x.value * p1.x.value * (p2.y.value - p3.y.value)

    val vertexXCoordinate get() = -b / (2 * a)
}