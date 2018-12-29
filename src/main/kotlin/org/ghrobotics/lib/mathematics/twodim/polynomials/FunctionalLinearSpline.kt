package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class FunctionalLinearSpline(
    val p1: Translation2d,
    val p2: Translation2d
) {
    val m get() = (p2._y - p1._y) / (p2._x - p1._x)
    val b get() = p1._y - (m * p1._x)

    val zero get() = -b / m
}