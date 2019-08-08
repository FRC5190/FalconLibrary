package org.ghrobotics.lib.mathematics.twodim.polynomials

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.Mult
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.operations.times

data class FunctionalQuadraticSpline(
    private val p1: Translation2d,
    private val p2: Translation2d,
    private val p3: Translation2d
) {
    private val a: SIUnit<Mult<Meter, Meter>>
        get() = p3.x * (p2.y - p1.y) + p2.x * (p1.y - p3.y) + p1.x * (p3.y - p2.y)
    private val b: SIUnit<Mult<Mult<Meter, Meter>, Meter>>
        get() = p3.x * p3.x * (p1.y - p2.y) + p2.x * p2.x * (p3.y - p1.y) + p1.x * p1.x * (p2.y - p3.y)

    val vertexXCoordinate: SIUnit<Meter> get() = -b / (2.0 * a)
}