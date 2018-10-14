package org.ghrobotics.lib.mathematics.units.expressions

import org.ghrobotics.lib.mathematics.units.AbstractSIValue

abstract class SIExpression<T : SIExpression<T>> : AbstractSIValue<T>() {
    override fun plus(other: T): T {
        TODO("not implemented")
    }

    override fun minus(other: T): T {
        TODO("not implemented")
    }

    override fun div(other: T): Double {
        TODO("not implemented")
    }

    override fun compareTo(other: T): Int {
        TODO("not implemented")
    }
}