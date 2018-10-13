package org.ghrobotics.lib.mathematics.units

interface SIBaseFraction<T : SIValue<T>, B : SIUnit<B, BE>, BE : Enum<BE>, O> : SIValue<O> {
    val top: T
    val bottom: B

    fun adjustBottom(bottomUnits: BE, bottomPrefix: SIPrefix = SIPrefix.BASE): O
}

interface SIFraction<T : SIUnit<T, TE>, TE : Enum<TE>, B : SIUnit<B, BE>, BE : Enum<BE>, O> :
    SIBaseFraction<T, B, BE, O> {

    fun adjust(topUnits: TE, bottomUnits: BE) = adjust(topUnits, SIPrefix.BASE, bottomUnits, SIPrefix.BASE)
    fun adjust(topUnits: TE, topPrefix: SIPrefix, bottomUnits: BE, bottomPrefix: SIPrefix): O

    operator fun times(other: B): T
}

interface SIChainedFraction<T : SIBaseFraction<*, *, *, T>, B : SIUnit<B, BE>, BE : Enum<BE>, O> :
    SIBaseFraction<T, B, BE, O> {
    operator fun times(other: B): T
}

abstract class AbstractBaseSIFraction<T : SIValue<T>, B : SIUnit<B, BE>, BE : Enum<BE>, O>(
    final override val top: T,
    final override val bottom: B
) : AbstractSIValue<O>(), SIBaseFraction<T, B, BE, O> {

    override val asDouble = top.asDouble / bottom.asDouble
    override val absoluteValue by lazy { create(top.absoluteValue, bottom.absoluteValue) }

    override fun unaryMinus() = create(-top, bottom)

    override val asMetric by lazy { create(top.asMetric, bottom.asMetric) }

    override fun adjustBottom(
        bottomUnits: BE,
        bottomPrefix: SIPrefix
    ): O = create(
        top,
        bottom.convertTo(bottomPrefix, bottomUnits)
    )

    abstract fun create(newTop: T, newBottom: B): O
    @Suppress("UNCHECKED_CAST")
    fun createUnsafe(newTop: Any, newBottom: Any) = create(newTop as T, newBottom as B)

    override fun times(other: Number) = create(top * other, bottom)
    override fun div(other: Number) = create(top, bottom * other)

    override fun plus(other: O): O {
        TODO("not implemented")
    }

    override fun minus(other: O): O {
        TODO("not implemented")
    }

    override fun compareTo(other: O): Int {
        TODO("not implemented")
    }

    override fun toString() = buildString {
        append(top)
        append(" / ")
        append(bottom)
    }

}

abstract class AbstractSIFraction<T : SIUnit<T, TE>, TE : Enum<TE>, B : SIUnit<B, BE>, BE : Enum<BE>, O>(
    top: T,
    bottom: B
) : AbstractBaseSIFraction<T, B, BE, O>(top, bottom), SIFraction<T, TE, B, BE, O> {

    override fun adjust(
        topUnits: TE,
        topPrefix: SIPrefix,
        bottomUnits: BE,
        bottomPrefix: SIPrefix
    ): O = create(
        top.convertTo(topPrefix, topUnits),
        bottom.convertTo(bottomPrefix, bottomUnits)
    )

    @Suppress("UNCHECKED_CAST")
    override fun div(other: O): Double {
        val aN = this.top.asMetric
        val bN = this.bottom.asMetric

        val otherN = other as AbstractSIFraction<T, TE, B, BE, O>
        val cN = otherN.bottom.asMetric
        val dN = otherN.top.asMetric

        return (aN.asDouble * cN.asDouble) / (bN.asDouble * dN.asDouble)
    }

    override fun times(other: B): T {
        val otherN = other.asMetric
        val bottomN = bottom.asMetric
        return top * otherN.asDouble / bottomN.asDouble
    }
}

abstract class AbstractSIChainedFraction<T : SIBaseFraction<*, *, *, T>, B : SIUnit<B, BE>, BE : Enum<BE>, O>(
    top: T,
    bottom: B
) : AbstractBaseSIFraction<T, B, BE, O>(top, bottom), SIChainedFraction<T, B, BE, O> {

    override fun adjustBottom(
        bottomUnits: BE,
        bottomPrefix: SIPrefix
    ): O = create(
        top,
        bottom.convertTo(bottomPrefix, bottomUnits)
    )

    @Suppress("UNCHECKED_CAST")
    override fun div(other: O): Double {
        val aN = (this.top.top as SIValue<out SIValue<*>>).asMetric
        val bN = this.top.bottom.asMetric
        val cN = this.bottom.asMetric

        val otherN = other as AbstractSIChainedFraction<T, B, BE, O>
        val dN = otherN.top.bottom.asMetric
        val eN = otherN.bottom.asMetric
        val fN = (otherN.top.top as SIValue<out SIValue<*>>).asMetric

        return (aN.asDouble * dN.asDouble * eN.asDouble) / (bN.asDouble * cN.asDouble * fN.asDouble)
    }

    @Suppress("UNCHECKED_CAST")
    override fun times(other: B): T {
        val top = this.top as AbstractBaseSIFraction<*, *, *, *>
        // make both metric so they have the same unit making them cancel each other out
        val otherN = other.asMetric
        val bottomN = bottom.asMetric
        return top.createUnsafe(
            top.top as SIValue<out SIValue<*>> * otherN.asDouble,
            top.bottom * bottomN.asDouble
        ) as T
    }
}