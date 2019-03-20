package org.ghrobotics.lib.mathematics.units

val Number.thou get() = Length(toDouble() * SILengthConstants.kThouToMeter)
val Number.line get() = Length(toDouble() * SILengthConstants.kLineToMeter)
val Number.inch get() = Length(toDouble() * SILengthConstants.kInchToMeter)
val Number.feet get() = Length(toDouble() * SILengthConstants.kFeetToMeter)
val Number.yard get() = Length(toDouble() * SILengthConstants.kYardToMeter)
val Number.mile get() = Length(toDouble() * SILengthConstants.kMileToMeter)
val Number.league get() = Length(toDouble() * SILengthConstants.kLeagueToMeter)
val Number.nauticalMile get() = Length(toDouble() * SILengthConstants.kNauticalMile)
val Number.lightYear get() = Length(toDouble() * SILengthConstants.kLightYearToMeter)

val Number.yottameter get() = Length(toDouble() * SIConstants.kYotta)
val Number.zettameter get() = Length(toDouble() * SIConstants.kZetta)
val Number.exameter get() = Length(toDouble() * SIConstants.kExa)
val Number.petameter get() = Length(toDouble() * SIConstants.kPeta)
val Number.terameter get() = Length(toDouble() * SIConstants.kTera)
val Number.gigameter get() = Length(toDouble() * SIConstants.kGiga)
val Number.megameter get() = Length(toDouble() * SIConstants.kMega)
val Number.kilometer get() = Length(toDouble() * SIConstants.kKilo)
val Number.hectometer get() = Length(toDouble() * SIConstants.kHecto)
val Number.decameter get() = Length(toDouble() * SIConstants.kDeca)
val Number.meter get() = Length(toDouble())
val Number.decimeter get() = Length(toDouble() * SIConstants.kDeci)
val Number.centimeter get() = Length(toDouble() * SIConstants.kCenti)
val Number.millimeter get() = Length(toDouble() * SIConstants.kMilli)
val Number.micrometer get() = Length(toDouble() * SIConstants.kMicro)
val Number.nanometer get() = Length(toDouble() * SIConstants.kNano)
val Number.picometer get() = Length(toDouble() * SIConstants.kPico)
val Number.femtometer get() = Length(toDouble() * SIConstants.kFemto)
val Number.attometer get() = Length(toDouble() * SIConstants.kAtto)
val Number.zeptometer get() = Length(toDouble() * SIConstants.kZepto)
val Number.yoctometer get() = Length(toDouble() * SIConstants.kYocto)

object SILengthConstants {
    const val kInchToMeter = 0.0254
    const val kThouToMeter = kInchToMeter * 0.001
    const val kLineToMeter = kInchToMeter * (1.0 / 12.0)
    const val kFeetToMeter = kInchToMeter * 12
    const val kYardToMeter = kFeetToMeter * 3
    const val kMileToMeter = kFeetToMeter * 5280
    const val kLeagueToMeter = kMileToMeter * 3
    const val kNauticalMile = 1852
    const val kLightYearToMeter = 9460730472580800.0
}

class Length(
    override val value: Double
) : SIUnit<Length> {
    val thou get() = value / SILengthConstants.kThouToMeter
    val line get() = value / SILengthConstants.kLineToMeter
    val inch get() = value / SILengthConstants.kInchToMeter
    val feet get() = value / SILengthConstants.kFeetToMeter
    val yard get() = value / SILengthConstants.kYardToMeter
    val mile get() = value / SILengthConstants.kMileToMeter
    val league get() = value / SILengthConstants.kLeagueToMeter
    val nauticalMile get() = value / SILengthConstants.kNauticalMile
    val lightYear get() = value / SILengthConstants.kLightYearToMeter

    val yottameter get() = value / SIConstants.kYotta
    val zettameter get() = value / SIConstants.kZetta
    val exameter get() = value / SIConstants.kExa
    val petameter get() = value / SIConstants.kPeta
    val terameter get() = value / SIConstants.kTera
    val gigameter get() = value / SIConstants.kGiga
    val megameter get() = value / SIConstants.kMega
    val kilometer get() = value / SIConstants.kKilo
    val hectometer get() = value / SIConstants.kHecto
    val decameter get() = value / SIConstants.kDeca
    val meter get() = value
    val decimeter get() = value / SIConstants.kDeci
    val centimeter get() = value / SIConstants.kCenti
    val millimeter get() = value / SIConstants.kMilli
    val micrometer get() = value / SIConstants.kMicro
    val nanometer get() = value / SIConstants.kNano
    val picometer get() = value / SIConstants.kPico
    val femtometer get() = value / SIConstants.kFemto
    val attometer get() = value / SIConstants.kAtto
    val zeptometer get() = value / SIConstants.kZepto
    val yoctometer get() = value / SIConstants.kYocto

    override fun createNew(newValue: Double) = Length(newValue)

    companion object {
        val kZero = Length(0.0)
    }
}