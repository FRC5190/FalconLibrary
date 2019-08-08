package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.operations.div
import org.ghrobotics.lib.mathematics.units2.operations.times

@Suppress("unused", "MemberVisibilityCanBePrivate")
data class FunctionalLinearSpline(
    val p1: Translation2d,
    val p2: Translation2d
) {
    val m: Double get() = ((p2.y - p1.y) / (p2.x - p1.x)).value
    val b: SIUnit<Meter> get() = p1.y - (m * p1.x)

    val zero: SIUnit<Meter> get() = -b / m
}