package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

@Suppress("unused", "MemberVisibilityCanBePrivate")
class FunctionalLinearSpline(val p1: Translation2d, val p2: Translation2d) {
    val m get() = (p2.yRaw - p1.yRaw) / (p2.xRaw - p1.xRaw)
    val b get() = p1.yRaw - (m * p1.xRaw)

    val zero get() = -b / m
}