package org.ghrobotics.lib.mathematics.units

val Number.meter by length(LengthUnits.Meter)
val Number.thou by length(LengthUnits.Thou)
val Number.line by length(LengthUnits.Line)
val Number.inch by length(LengthUnits.Inch)
val Number.feet by length(LengthUnits.Feet)
val Number.yard by length(LengthUnits.Yard)
val Number.mile by length(LengthUnits.Mile)
val Number.league by length(LengthUnits.League)
val Number.lightYear by length(LengthUnits.LightYear)

private fun length(type: LengthUnits) = AbstractLength.createDelegate(type)

val Number.yottameter by meter(SIPrefix.YOTTA)
val Number.zettameter by meter(SIPrefix.ZETTA)
val Number.exameter by meter(SIPrefix.EXA)
val Number.petameter by meter(SIPrefix.PETA)
val Number.terameter by meter(SIPrefix.TERA)
val Number.gigameter by meter(SIPrefix.GIGA)
val Number.megameter by meter(SIPrefix.MEGA)
val Number.kilometer by meter(SIPrefix.KILO)
val Number.hectometer by meter(SIPrefix.HECTO)
val Number.decameter by meter(SIPrefix.DECA)
val Number.decimeter by meter(SIPrefix.DECI)
val Number.centimeter by meter(SIPrefix.CENTI)
val Number.millimeter by meter(SIPrefix.MILLI)
val Number.micrometer by meter(SIPrefix.MICRO)
val Number.nanometer by meter(SIPrefix.NANO)
val Number.picometer by meter(SIPrefix.PICO)
val Number.femtometer by meter(SIPrefix.FEMTO)
val Number.attometer by meter(SIPrefix.ATTO)
val Number.zeptometer by meter(SIPrefix.ZEPTO)
val Number.yoctometer by meter(SIPrefix.YOCTO)

private fun meter(prefix: SIPrefix) = AbstractLength.createMetricDelegate(prefix)

enum class LengthUnits {
    Meter,
    Thou,
    Line,
    Inch,
    Feet,
    Yard,
    Mile,
    League,
    NauticalMile,
    LightYear
}

interface Length : SIUnit<Length, LengthUnits> {
    val meter: Length
    val thou: Length
    val line: Length
    val inch: Length
    val feet: Length
    val yard: Length
    val mile: Length
    val league: Length
    val nauticalMile: Length
    val lightYear: Length

    val yottameter: Length
    val zettameter: Length
    val exameter: Length
    val petameter: Length
    val terameter: Length
    val gigameter: Length
    val megameter: Length
    val kilometer: Length
    val hectometer: Length
    val decameter: Length
    val decimeter: Length
    val centimeter: Length
    val millimeter: Length
    val micrometer: Length
    val nanometer: Length
    val picometer: Length
    val femtometer: Length
    val attometer: Length
    val zeptometer: Length
    val yoctometer: Length
}

class AbstractLength(
        value: Double,
        prefix: SIPrefix,
        type: LengthUnits
) : AbstractSIUnit<Length, LengthUnits>(
        value,
        prefix,
        type,
        AbstractLength
), Length {
    override val meter by convertUnit(LengthUnits.Meter)
    override val thou by convertUnit(LengthUnits.Thou)
    override val line by convertUnit(LengthUnits.Line)
    override val inch by convertUnit(LengthUnits.Inch)
    override val feet by convertUnit(LengthUnits.Feet)
    override val yard by convertUnit(LengthUnits.Yard)
    override val mile by convertUnit(LengthUnits.Mile)
    override val league by convertUnit(LengthUnits.League)
    override val nauticalMile by convertUnit(LengthUnits.NauticalMile)
    override val lightYear by convertUnit(LengthUnits.LightYear)

    override val yottameter by convertMetric(SIPrefix.YOTTA)
    override val zettameter by convertMetric(SIPrefix.ZETTA)
    override val exameter by convertMetric(SIPrefix.EXA)
    override val petameter by convertMetric(SIPrefix.PETA)
    override val terameter by convertMetric(SIPrefix.TERA)
    override val gigameter by convertMetric(SIPrefix.GIGA)
    override val megameter by convertMetric(SIPrefix.MEGA)
    override val kilometer by convertMetric(SIPrefix.KILO)
    override val hectometer by convertMetric(SIPrefix.HECTO)
    override val decameter by convertMetric(SIPrefix.DECA)
    override val decimeter by convertMetric(SIPrefix.DECI)
    override val centimeter by convertMetric(SIPrefix.CENTI)
    override val millimeter by convertMetric(SIPrefix.MILLI)
    override val micrometer by convertMetric(SIPrefix.MICRO)
    override val nanometer by convertMetric(SIPrefix.NANO)
    override val picometer by convertMetric(SIPrefix.PICO)
    override val femtometer by convertMetric(SIPrefix.FEMTO)
    override val attometer by convertMetric(SIPrefix.ATTO)
    override val zeptometer by convertMetric(SIPrefix.ZEPTO)
    override val yoctometer by convertMetric(SIPrefix.YOCTO)

    companion object : SIUnitConverter<Length, LengthUnits>(
            LengthUnits.Meter,
            UnitMapper.lengthMapper
    ) {
        override fun create(newValue: Double, newPrefix: SIPrefix, newType: LengthUnits): Length =
                AbstractLength(newValue, newPrefix, newType)
    }

}