package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class FunctionalLinearSpline(
    val p1: Translation2d,
    val p2: Translation2d
) {
    val m get() = (p2.y - p1.y) / (p2.x - p1.x)
    val b get() = p1.y - (m * p1.x)

    val zero get() = -b / m
}