package org.ghrobotics.lib.mathematics.units

val Number.minute get() = Time(toDouble() * SITimeConstants.kMinuteToSecond)
val Number.hour get() = Time(toDouble() * SITimeConstants.kHourToSecond)
val Number.day get() = Time(toDouble() * SITimeConstants.kDayToSecond)
val Number.week get() = Time(toDouble() * SITimeConstants.kWeekToSecond)
val Number.moment get() = Time(toDouble() * SITimeConstants.kMomentToSecond)

val Number.yottasecond get() = Time(toDouble() * SIConstants.kYotta)
val Number.zettasecond get() = Time(toDouble() * SIConstants.kZetta)
val Number.exasecond get() = Time(toDouble() * SIConstants.kExa)
val Number.petasecond get() = Time(toDouble() * SIConstants.kPeta)
val Number.terasecond get() = Time(toDouble() * SIConstants.kTera)
val Number.gigasecond get() = Time(toDouble() * SIConstants.kGiga)
val Number.megasecond get() = Time(toDouble() * SIConstants.kMega)
val Number.kilosecond get() = Time(toDouble() * SIConstants.kKilo)
val Number.hectosecond get() = Time(toDouble() * SIConstants.kHecto)
val Number.decasecond get() = Time(toDouble() * SIConstants.kDeca)
val Number.second get() = Time(toDouble())
val Number.decisecond get() = Time(toDouble() * SIConstants.kDeci)
val Number.centisecond get() = Time(toDouble() * SIConstants.kCenti)
val Number.millisecond get() = Time(toDouble() * SIConstants.kMilli)
val Number.microsecond get() = Time(toDouble() * SIConstants.kMicro)
val Number.nanosecond get() = Time(toDouble() * SIConstants.kNano)
val Number.picosecond get() = Time(toDouble() * SIConstants.kPico)
val Number.femtosecond get() = Time(toDouble() * SIConstants.kFemto)
val Number.attosecond get() = Time(toDouble() * SIConstants.kAtto)
val Number.zeptosecond get() = Time(toDouble() * SIConstants.kZepto)
val Number.yoctosecond get() = Time(toDouble() * SIConstants.kYocto)

object SITimeConstants {
    const val kMinuteToSecond = 60
    const val kHourToSecond = kMinuteToSecond * 60
    const val kDayToSecond = kHourToSecond * 24
    const val kWeekToSecond = kDayToSecond * 7
    const val kMomentToSecond = 90
}

class Time(
    override val value: Double
) : SIUnit<Time> {
    val minute get() = value / SITimeConstants.kMinuteToSecond
    val hour get() = value / SITimeConstants.kHourToSecond
    val day get() = value / SITimeConstants.kDayToSecond
    val week get() = value / SITimeConstants.kWeekToSecond
    val moment get() = value / SITimeConstants.kMomentToSecond

    val yottasecond get() = value / SIConstants.kYotta
    val zettasecond get() = value / SIConstants.kZetta
    val exasecond get() = value / SIConstants.kExa
    val petasecond get() = value / SIConstants.kPeta
    val terasecond get() = value / SIConstants.kTera
    val gigasecond get() = value / SIConstants.kGiga
    val megasecond get() = value / SIConstants.kMega
    val kilosecond get() = value / SIConstants.kKilo
    val hectosecond get() = value / SIConstants.kHecto
    val decasecond get() = value / SIConstants.kDeca
    val second get() = value
    val decisecond get() = value / SIConstants.kDeci
    val centisecond get() = value / SIConstants.kCenti
    val millisecond get() = value / SIConstants.kMilli
    val microsecond get() = value / SIConstants.kMicro
    val nanosecond get() = value / SIConstants.kNano
    val picosecond get() = value / SIConstants.kPico
    val femtosecond get() = value / SIConstants.kFemto
    val attosecond get() = value / SIConstants.kAtto
    val zeptosecond get() = value / SIConstants.kZepto
    val yoctosecond get() = value / SIConstants.kYocto

    override fun createNew(newValue: Double) = Time(newValue)

    companion object {
        val kZero = Time(0.0)
    }
}