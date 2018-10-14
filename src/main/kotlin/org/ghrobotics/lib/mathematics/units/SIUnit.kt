package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.units.expressions.SIExp2
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface SIUnit<T : SIUnit<T, E>, E : Enum<E>> : SIValue<T> {
    val prefix: SIPrefix
    val type: E

    @Suppress("UNCHECKED_CAST")
    operator fun <B : SIUnit<B, *>> times(other: B) = SIExp2(this as T, other)

    infix fun <B : SIUnit<B, *>> per(other: B) = div(other)
    @Suppress("UNCHECKED_CAST")
    operator fun <B : SIUnit<B, *>> div(other: B) = SIFrac11(this as T, other)

    fun convertTo(newPrefix: SIPrefix, newUnit: E): T
}

abstract class SIUnitConverter<T : SIUnit<T, E>, E : Enum<E>>(
    val metricUnit: E,
    val mapper: UnitMapper.SpecificUnitMapper<E>
) {

    abstract fun create(newValue: Double, newPrefix: SIPrefix, newType: E): T

    fun convertValue(value: T, to: E) =
        mapper.convert(
            value.asDouble,
            value.type,
            to
        )

    fun convertTo(value: T, newPrefix: SIPrefix, newUnit: E): T {
        if (newUnit != metricUnit && newPrefix != SIPrefix.BASE) throw IllegalArgumentException("Only metric units can have prefixes!")
        val convertedValue = convertValue(value, newUnit)
        val prefixedValue =
            SIPrefix.convertPrefix(convertedValue, value.prefix, newPrefix)
        return create(prefixedValue, newPrefix, newUnit)
    }

    fun createDelegate(prefix: SIPrefix, type: E) = object : ReadOnlyProperty<Number, T> {
        override fun getValue(thisRef: Number, property: KProperty<*>) = create(thisRef.toDouble(), prefix, type)
    }

    fun createDelegate(type: E) = createDelegate(SIPrefix.BASE, type)
    fun createMetricDelegate(prefix: SIPrefix) = createDelegate(prefix, metricUnit)
}

abstract class AbstractSIUnit<T : SIUnit<T, E>, E : Enum<E>>(
    value: Double,
    override val prefix: SIPrefix,
    override val type: E,
    val converter: SIUnitConverter<T, E>
) : AbstractSIValue<T>(), SIUnit<T, E> {

    override val asDouble = value

    override val asMetric by convertMetric(SIPrefix.BASE)

    override val absoluteValue: T by lazy { converter.create(asDouble.absoluteValue, prefix, type) }

    private fun createAndAdjust(currentPrefix: SIPrefix, newValue: Double): T {
        val newPrefix = SIPrefix.findOptimalPrefix(newValue, currentPrefix)
        return converter.create(SIPrefix.convertPrefix(newValue, currentPrefix, newPrefix), newPrefix, type)
    }

    private fun convertValue(other: T) = converter.convertValue(other, type)

    override fun unaryMinus(): T = converter.create(-asDouble, prefix, type)

    override fun plus(other: T): T = mathOperation(other) { one, two -> one + two }
    override fun minus(other: T): T = mathOperation(other) { one, two -> one - two }

    override fun div(other: T) = operation(other) { one, two -> one / two }

    override fun times(other: Number) = createAndAdjust(prefix, asDouble * other.toDouble())
    override fun div(other: Number) = createAndAdjust(prefix, asDouble / other.toDouble())

    override fun compareTo(other: T) = operation(other) { one, two -> one.compareTo(two) }

    private fun mathOperation(other: T, block: (Double, Double) -> Double): T =
        createAndAdjust(SIPrefix.BASE, operation(other, block))

    private fun <V> operation(other: T, block: (Double, Double) -> V): V =
        block(prefix.apply(asDouble), other.prefix.apply(convertValue(other)))

    protected fun convertMetric(newPrefix: SIPrefix) =
        convertDelegate(newPrefix, converter.metricUnit)

    protected fun convertUnit(newUnit: E) =
        convertDelegate(SIPrefix.BASE, newUnit)

    private fun convertDelegate(
        newPrefix: SIPrefix,
        newUnit: E
    ) = lazy { convertTo(newPrefix, newUnit) }

    override fun convertTo(newPrefix: SIPrefix, newUnit: E) =
        @Suppress("UNCHECKED_CAST")
        converter.convertTo(this as T, newPrefix, newUnit)

    override fun toString() = buildString {
        append(asDouble)
        append(' ')
        if (prefix != SIPrefix.BASE) append(prefix.name.toLowerCase())
        append(type.name.toLowerCase())
    }

}

enum class SIPrefix(val exponent: Int) {
    YOTTA(24),
    ZETTA(21),
    EXA(18),
    PETA(15),
    TERA(12),
    GIGA(9),
    MEGA(6),
    KILO(3),
    HECTO(2),
    DECA(1),
    BASE(0),
    DECI(-1),
    CENTI(-2),
    MILLI(-3),
    MICRO(-6),
    NANO(-9),
    PICO(-12),
    FEMTO(-15),
    ATTO(-18),
    ZEPTO(-21),
    YOCTO(-24);

    fun apply(value: Double) = apply(value, this)

    companion object {
        fun apply(value: Double, prefix: SIPrefix) = value * 10.0.pow(prefix.exponent)

        fun findOptimalPrefix(
            value: Double,
            currentPrefix: SIPrefix = BASE
        ): SIPrefix {
            val exponent = Math.log10(value).toInt() + currentPrefix.exponent
            return if (exponent > 0) {
                values().run {
                    firstOrNull { it.exponent >= exponent } ?: first()
                }
            } else {
                values().run {
                    lastOrNull { it.exponent <= exponent } ?: last()
                }
            }
        }

        fun convertPrefix(value: Double, oldPrefix: SIPrefix, newPrefix: SIPrefix): Double {
            val exponent = oldPrefix.exponent - newPrefix.exponent
            return value * 10.0.pow(exponent)
        }
    }
}