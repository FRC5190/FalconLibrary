package org.ghrobotics.lib.mathematics.units

val Number.second by time(TimeUnits.Second)
val Number.minute by time(TimeUnits.Minute)
val Number.hour by time(TimeUnits.Hour)
val Number.day by time(TimeUnits.Day)
val Number.week by time(TimeUnits.Week)
val Number.moment by time(TimeUnits.Moment)

private fun time(type: TimeUnits) = AbstractTime.createDelegate(type)

val Number.yottasecond by second(SIPrefix.YOTTA)
val Number.zettasecond by second(SIPrefix.ZETTA)
val Number.exasecond by second(SIPrefix.EXA)
val Number.petasecond by second(SIPrefix.PETA)
val Number.terasecond by second(SIPrefix.TERA)
val Number.gigasecond by second(SIPrefix.GIGA)
val Number.megasecond by second(SIPrefix.MEGA)
val Number.kilosecond by second(SIPrefix.KILO)
val Number.hectosecond by second(SIPrefix.HECTO)
val Number.decasecond by second(SIPrefix.DECA)
val Number.decisecond by second(SIPrefix.DECI)
val Number.centisecond by second(SIPrefix.CENTI)
val Number.millisecond by second(SIPrefix.MILLI)
val Number.microsecond by second(SIPrefix.MICRO)
val Number.nanosecond by second(SIPrefix.NANO)
val Number.picosecond by second(SIPrefix.PICO)
val Number.femtosecond by second(SIPrefix.FEMTO)
val Number.attosecond by second(SIPrefix.ATTO)
val Number.zeptosecond by second(SIPrefix.ZEPTO)
val Number.yoctosecond by second(SIPrefix.YOCTO)

private fun second(prefix: SIPrefix) = AbstractTime.createMetricDelegate(prefix)

enum class TimeUnits {
    Second,
    Minute,
    Hour,
    Day,
    Week,
    Moment
}

interface Time : SIUnit<Time, TimeUnits> {
    val second: Time
    val minute: Time
    val hour: Time
    val day: Time
    val week: Time
    val moment: Time

    val yottasecond: Time
    val zettasecond: Time
    val exasecond: Time
    val petasecond: Time
    val terasecond: Time
    val gigasecond: Time
    val megasecond: Time
    val kilosecond: Time
    val hectosecond: Time
    val decasecond: Time
    val decisecond: Time
    val centisecond: Time
    val millisecond: Time
    val microsecond: Time
    val nanosecond: Time
    val picosecond: Time
    val femtosecond: Time
    val attosecond: Time
    val zeptosecond: Time
    val yoctosecond: Time
}

class AbstractTime(
        value: Double,
        prefix: SIPrefix,
        type: TimeUnits
) : AbstractSIUnit<Time, TimeUnits>(
        value,
        prefix,
        type,
        AbstractTime
), Time {
    override val second by convertUnit(TimeUnits.Second)
    override val minute by convertUnit(TimeUnits.Minute)
    override val hour by convertUnit(TimeUnits.Hour)
    override val day by convertUnit(TimeUnits.Day)
    override val week by convertUnit(TimeUnits.Week)
    override val moment by convertUnit(TimeUnits.Moment)

    override val yottasecond by convertMetric(SIPrefix.YOTTA)
    override val zettasecond by convertMetric(SIPrefix.ZETTA)
    override val exasecond by convertMetric(SIPrefix.EXA)
    override val petasecond by convertMetric(SIPrefix.PETA)
    override val terasecond by convertMetric(SIPrefix.TERA)
    override val gigasecond by convertMetric(SIPrefix.GIGA)
    override val megasecond by convertMetric(SIPrefix.MEGA)
    override val kilosecond by convertMetric(SIPrefix.KILO)
    override val hectosecond by convertMetric(SIPrefix.HECTO)
    override val decasecond by convertMetric(SIPrefix.DECA)
    override val decisecond by convertMetric(SIPrefix.DECI)
    override val centisecond by convertMetric(SIPrefix.CENTI)
    override val millisecond by convertMetric(SIPrefix.MILLI)
    override val microsecond by convertMetric(SIPrefix.MICRO)
    override val nanosecond by convertMetric(SIPrefix.NANO)
    override val picosecond by convertMetric(SIPrefix.PICO)
    override val femtosecond by convertMetric(SIPrefix.FEMTO)
    override val attosecond by convertMetric(SIPrefix.ATTO)
    override val zeptosecond by convertMetric(SIPrefix.ZEPTO)
    override val yoctosecond by convertMetric(SIPrefix.YOCTO)

    companion object : SIUnitConverter<Time, TimeUnits>(
            TimeUnits.Second,
            UnitMapper.timeMapper
    ) {
        override fun create(newValue: Double, newPrefix: SIPrefix, newType: TimeUnits): Time =
                AbstractTime(newValue, newPrefix, newType)
    }

}