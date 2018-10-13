package org.ghrobotics.lib.mathematics.onedim.geometry

import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.types.VaryInterpolatable
import java.text.DecimalFormat

class Displacement1d(val x: Double) : VaryInterpolatable<Displacement1d> {

    constructor() : this(0.0)

    fun addDisplacement(other: Displacement1d) = Displacement1d(x + other.x)

    override fun distance(other: Displacement1d): Double {
        return other.x - this.x
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is Displacement1d) false else distance(other) < kEpsilon
    }

    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return "(" + fmt.format(x) + ")"
    }

    fun toCSV(): String {
        val fmt = DecimalFormat("#0.000")
        return fmt.format(x)
    }

    override fun interpolate(upperVal: Displacement1d, interpolatePoint: Double): Displacement1d {
        return this
    }
}