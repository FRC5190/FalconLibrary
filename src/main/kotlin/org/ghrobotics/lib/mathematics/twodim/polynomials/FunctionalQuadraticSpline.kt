package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

data class FunctionalQuadraticSpline(
    private val p1: Translation2d,
    private val p2: Translation2d,
    private val p3: Translation2d
) {
    private val a get() = p3._x * (p2._y - p1._y) + p2._x * (p1._y - p3._y) + p1._x * (p3._y - p2._y)
    private val b get() = p3._x * p3._x * (p1._y - p2._y) + p2._x * p2._x * (p3._y - p1._y) + p1._x * p1._x * (p2._y - p3._y)

    val vertexXCoordinate get() = -b / (2 * a)
}