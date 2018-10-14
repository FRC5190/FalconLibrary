package org.ghrobotics.lib.mathematics.units.fractions

import org.ghrobotics.lib.mathematics.units.AbstractSIValue
import org.ghrobotics.lib.mathematics.units.SIValue

interface SIFrac<T : SIValue<T>, B : SIValue<B>, O : SIValue<O>> : SIValue<O> {
    val top: T
    val bottom: B
}

abstract class AbstractSIFrac<T : SIValue<T>, B : SIValue<B>, O : SIValue<O>>(
    override val top: T,
    override val bottom: B
) : AbstractSIValue<O>(), SIFrac<T,B,O> {

    override val asDouble: Double
        get() = top.asDouble / bottom.asDouble
    override val asMetric: O
        get() = create(top.asMetric, bottom.asMetric)
    override val absoluteValue: O
        get() = create(top.absoluteValue, bottom.absoluteValue)

    abstract fun create(newTop: T, newBottom: B): O

    override fun unaryMinus(): O = create(-top, bottom)

    override fun plus(other: O): O {
        TODO("not implemented")
    }

    override fun minus(other: O): O {
        TODO("not implemented")
    }

    override fun times(other: Number): O = create(top * other, bottom)
    override fun div(other: Number): O = create(top, bottom * other)

    override fun compareTo(other: O): Int {
        TODO("not implemented")
    }

}