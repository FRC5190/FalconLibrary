package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.millisecond
import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun Length.STU(settings: NativeUnitSettings) =
    NativeUnitImpl.distanceToNativeUnit(this, settings)

val Number.STU: NativeUnit
    get() = NativeUnitImpl(toLong())

fun Number.STU(settings: NativeUnitSettings) = STU.length(settings)

interface NativeUnit :
    SIValue<NativeUnit> {
    fun length(settings: NativeUnitSettings): Length

    fun plus(other: Length, settings: NativeUnitSettings): NativeUnit
    fun minus(other: Length, settings: NativeUnitSettings): NativeUnit
}

data class NativeUnitSettings(
    val sensorUnitsPerRotation: Int = 1440,
    val radius: Double = 3.0
)

private class NativeUnitImpl(val value: Long) : NativeUnit {
    override val asDouble: Double
        get() = value.toDouble()
    override val asFloat: Float
        get() = value.toFloat()
    override val asLong: Long
        get() = value
    override val asInt: Int
        get() = value.toInt()

    override val asMetric: NativeUnit
        get() = TODO("Native Unit cannot be represented as metric")

    override fun length(settings: NativeUnitSettings) = convertToInch(
        value,
        settings
    ).inch

    override val absoluteValue by lazy { NativeUnitImpl(value.absoluteValue) }

    override fun plus(other: NativeUnit) =
        NativeUnitImpl(value + other.asLong)

    override fun minus(other: NativeUnit) =
        NativeUnitImpl(value - other.asLong)

    override fun plus(other: Length, settings: NativeUnitSettings): NativeUnit =
        plus(
            distanceToNativeUnit(
                other,
                settings
            )
        )

    override fun minus(other: Length, settings: NativeUnitSettings) = plus(-other, settings)

    override fun times(other: Number) =
        NativeUnitImpl(value * other.toLong())
    override fun div(other: Number) =
        NativeUnitImpl(value * other.toLong())

    override fun div(other: NativeUnit) = (value / other.asLong).toDouble()

    override fun unaryMinus() = NativeUnitImpl(-value)

    override fun compareTo(other: NativeUnit) = value.compareTo(other.asLong)

    companion object {
        fun convertToInch(value: Long, settings: NativeUnitSettings): Double =
            value.toDouble() / settings.sensorUnitsPerRotation.toDouble() * (2.0 * Math.PI * settings.radius)

        fun distanceToNativeUnit(distance: Length, settings: NativeUnitSettings): NativeUnit =
            NativeUnitImpl(
                distanceToNativeUnitVal(
                    distance,
                    settings
                )
            )

        fun distanceToNativeUnitVal(distance: Length, settings: NativeUnitSettings): Long =
            (distance.inch.asDouble / (2.0 * Math.PI * settings.radius) * settings.sensorUnitsPerRotation.toDouble()).roundToLong()
    }
}