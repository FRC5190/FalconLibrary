package org.ghrobotics.lib.mathematics.units

val Number.second get() = Time(toDouble())
val Number.minute get() = (toDouble() * 60).second
val Number.hour get() = (toDouble() * 60).minute
val Number.day get() = (toDouble() * 24).hour
val Number.week get() = (toDouble() * 7).day
val Number.moment get() = (toDouble() * 90.0).second

val Number.yottasecond get() = (toDouble() / 1e-24).second
val Number.zettasecond get() = (toDouble() / 1e-21).second
val Number.exasecond get() = (toDouble() / 1e-18).second
val Number.petasecond get() = (toDouble() / 1e-15).second
val Number.terasecond get() = (toDouble() / 1e-12).second
val Number.gigasecond get() = (toDouble() / 1e-9).second
val Number.megasecond get() = (toDouble() / 1e-6).second
val Number.kilosecond get() = (toDouble() / 0.001).second
val Number.hectosecond get() = (toDouble() / 0.01).second
val Number.decasecond get() = (toDouble() / 0.1).second
val Number.decisecond get() = (toDouble() / 10).second
val Number.centisecond get() = (toDouble() / 100).second
val Number.millisecond get() = (toDouble() / 1000).second
val Number.microsecond get() = (toDouble() / 1000000).second
val Number.nanosecond get() = (toDouble() / 1e+9).second
val Number.picosecond get() = (toDouble() / 1e+12).second
val Number.femtosecond get() = (toDouble() / 1e+15).second
val Number.attosecond get() = (toDouble() / 1e+18).second
val Number.zeptosecond get() = (toDouble() / 1e+21).second
val Number.yoctosecond get() = (toDouble() / 1e+24).second

class Time(
    override val value: Double
) : SIUnit<Time> {
    val second get() = value
    val minute get() = second / 60
    val hour get() = minute / 60
    val day get() = hour / 24
    val week get() = day / 7
    val moment get() = second / 90

    val yottasecond get() = value * 1e-24
    val zettasecond get() = value * 1e-21
    val exasecond get() = value * 1e-18
    val petasecond get() = value * 1e-15
    val terasecond get() = value * 1e-12
    val gigasecond get() = value * 1e-9
    val megasecond get() = value * 1e-6
    val kilosecond get() = value * 0.001
    val hectosecond get() = value * 0.01
    val decasecond get() = value * 0.1
    val decisecond get() = value * 10
    val centisecond get() = value * 100
    val millisecond get() = value * 1000
    val microsecond get() = value * 1000000
    val nanosecond get() = value * 1e+9
    val picosecond get() = value * 1e+12
    val femtosecond get() = value * 1e+15
    val attosecond get() = value * 1e+18
    val zeptosecond get() = value * 1e+21
    val yoctosecond get() = value * 1e+24

    override fun createNew(newBaseValue: Double) = Time(newBaseValue)
}