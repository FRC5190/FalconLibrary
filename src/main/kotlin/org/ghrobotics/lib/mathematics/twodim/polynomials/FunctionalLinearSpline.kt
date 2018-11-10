package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

@Suppress("unused", "MemberVisibilityCanBePrivate")
class FunctionalLinearSpline(val p1: Translation2d, val p2: Translation2d) {
    val m get() = (p2.y.value - p1.y.value) / (p2.x.value - p1.x.value)
    val b get() = p1.y.value - (m * p1.x.value)

    val zero get() = -b / m
}