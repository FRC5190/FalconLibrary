package org.ghrobotics.lib.mathematics.units

import kotlin.math.absoluteValue
import kotlin.math.roundToLong

fun Distance.STU(settings: NativeUnitSettings) = NativeUnitImpl.distanceToNativeUnit(this, settings)

val Double.STU: NativeUnit
    get() = toLong().STU
val Float.STU: NativeUnit
    get() = toLong().STU
val Long.STU: NativeUnit
    get() = NativeUnitImpl(this)
val Int.STU: NativeUnit
    get() = toLong().STU

fun Double.STU(settings: NativeUnitSettings) = toLong().STU(settings)
fun Float.STU(settings: NativeUnitSettings) = toLong().STU(settings)
fun Long.STU(settings: NativeUnitSettings): NativeUnitWithSettings =
        NativeUnitWithSettingsImpl(NativeUnitImpl(this), settings)
fun Int.STU(settings: NativeUnitSettings) = toLong().STU(settings)

interface NativeUnit {
    val asDouble: Double
    val asFloat: Float
    val asLong: Long
    val asInt: Int

    fun feet(settings: NativeUnitSettings): Distance
    fun inch(settings: NativeUnitSettings): Distance
    fun meter(settings: NativeUnitSettings): Distance

    val absoluteValue: NativeUnit

    operator fun plus(other: NativeUnit): NativeUnit
    operator fun minus(other: NativeUnit): NativeUnit

    fun plus(other: Distance, settings: NativeUnitSettings): NativeUnit
    fun minus(other: Distance, settings: NativeUnitSettings): NativeUnit

    operator fun times(scalar: Int): NativeUnit
    operator fun div(scalar: Int): NativeUnit

    operator fun unaryPlus(): NativeUnit = this
    operator fun unaryMinus(): NativeUnit

    fun withSettings(settings: NativeUnitSettings): NativeUnitWithSettings
}

interface NativeUnitWithSettings : Distance {
    val settings: NativeUnitSettings

    override val absoluteValue: NativeUnitWithSettings

    operator fun plus(other: NativeUnit): NativeUnitWithSettings
    operator fun minus(other: NativeUnit): NativeUnitWithSettings
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

    override fun feet(settings: NativeUnitSettings) = (convertToInch(value, settings) / 12.0).FT
    override fun inch(settings: NativeUnitSettings) = convertToInch(value, settings).IN
    override fun meter(settings: NativeUnitSettings) = (convertToInch(value, settings) / 12.0 / 3.28084).M

    override val absoluteValue by lazy { NativeUnitImpl(value.absoluteValue) }

    override fun plus(other: NativeUnit) = NativeUnitImpl(value + (other as NativeUnitImpl).value)
    override fun minus(other: NativeUnit) = NativeUnitImpl(value - (other as NativeUnitImpl).value)

    override fun plus(other: Distance, settings: NativeUnitSettings): NativeUnit =
        plus(distanceToNativeUnit(other, settings))

    override fun minus(other: Distance, settings: NativeUnitSettings) = plus(-other, settings)

    override fun times(scalar: Int) = NativeUnitImpl(value * scalar)
    override fun div(scalar: Int) = NativeUnitImpl(value * scalar)

    override fun unaryMinus() = NativeUnitImpl(-value)

    override fun withSettings(settings: NativeUnitSettings) = NativeUnitWithSettingsImpl(this, settings)

    companion object {
        fun convertToInch(value: Long, settings: NativeUnitSettings): Double =
            value.toDouble() / settings.sensorUnitsPerRotation.toDouble() * (2.0 * Math.PI * settings.radius)

        fun distanceToNativeUnit(distance: Distance, settings: NativeUnitSettings): NativeUnit =
            NativeUnitImpl(distanceToNativeUnitVal(distance, settings))

        fun distanceToNativeUnitVal(distance: Distance, settings: NativeUnitSettings): Long =
            (distance.IN.asDouble / (2.0 * Math.PI * settings.radius) * settings.sensorUnitsPerRotation.toDouble()).roundToLong()
    }
}

private class NativeUnitWithSettingsImpl(
    val nativeUnit: NativeUnitImpl,
    override val settings: NativeUnitSettings
) : NativeUnitWithSettings, LongDistance() {

    override val value: Long
        get() = nativeUnit.value

    override val FT by lazy { nativeUnit.feet(settings) }
    override val IN by lazy { nativeUnit.inch(settings) }
    override val M by lazy { nativeUnit.meter(settings) }

    override val absoluteValue
        get() = super.absoluteValue as NativeUnitWithSettings

    override fun plus(other: NativeUnit) = plus(other.withSettings(settings)) as NativeUnitWithSettings
    override fun minus(other: NativeUnit) = plus(other.withSettings(settings)) as NativeUnitWithSettings

    override fun create(newValue: Long) = NativeUnitWithSettingsImpl(NativeUnitImpl(newValue), settings)

    override fun convertToNum(other: Distance) =  NativeUnitImpl.distanceToNativeUnitVal(other, settings)
}