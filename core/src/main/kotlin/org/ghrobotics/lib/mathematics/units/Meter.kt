/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

object Meter : SIKey

const val kInchToMeter = 0.0254
const val kThouToMeter = kInchToMeter * 0.001
const val kLineToMeter = kInchToMeter * (1.0 / 12.0)
const val kFeetToMeter = kInchToMeter * 12.0
const val kYardToMeter = kFeetToMeter * 3.0
const val kMileToMeter = kFeetToMeter * 5280.0
const val kLeagueToMeter = kMileToMeter * 3.0
const val kNauticalMile = 1852.0
const val kLightYearToMeter = 9460730472580800.0

@Deprecated("Replaced with Plural version", ReplaceWith("meters"))
val Double.meter get() = meters
@Deprecated("Replaced with Plural version", ReplaceWith("lines"))
val Double.line get() = lines
@Deprecated("Replaced with Plural version", ReplaceWith("inches"))
val Double.inch get() = inches
@Deprecated("Replaced with Plural version", ReplaceWith("yards"))
val Double.yard get() = yards
@Deprecated("Replaced with Plural version", ReplaceWith("miles"))
val Double.mile get() = miles
@Deprecated("Replaced with Plural version", ReplaceWith("leagues"))
val Double.league get() = leagues
@Deprecated("Replaced with Plural version", ReplaceWith("nauticalMiles"))
val Double.nauticalMile get() = nauticalMiles
@Deprecated("Replaced with Plural version", ReplaceWith("lightYears"))
val Double.lightYear get() = lightYears

@Deprecated("Replaced with Plural version", ReplaceWith("meters"))
val Number.meter get() = meters
@Deprecated("Replaced with Plural version", ReplaceWith("lines"))
val Number.line get() = lines
@Deprecated("Replaced with Plural version", ReplaceWith("inches"))
val Number.inch get() = inches
@Deprecated("Replaced with Plural version", ReplaceWith("yards"))
val Number.yard get() = yards
@Deprecated("Replaced with Plural version", ReplaceWith("miles"))
val Number.mile get() = miles
@Deprecated("Replaced with Plural version", ReplaceWith("leagues"))
val Number.league get() = leagues
@Deprecated("Replaced with Plural version", ReplaceWith("nauticalMiles"))
val Number.nauticalMile get() = nauticalMiles
@Deprecated("Replaced with Plural version", ReplaceWith("lightYears"))
val Number.lightYear get() = lightYears

val Double.meters get() = SIUnit<Meter>(this)
val Double.thou get() = SIUnit<Meter>(times(kThouToMeter))
val Double.lines get() = SIUnit<Meter>(times(kLineToMeter))
val Double.inches get() = SIUnit<Meter>(times(kInchToMeter))
val Double.feet get() = SIUnit<Meter>(times(kFeetToMeter))
val Double.yards get() = SIUnit<Meter>(times(kYardToMeter))
val Double.miles get() = SIUnit<Meter>(times(kMileToMeter))
val Double.leagues get() = SIUnit<Meter>(times(kLeagueToMeter))
val Double.nauticalMiles get() = SIUnit<Meter>(times(kNauticalMile))
val Double.lightYears get() = SIUnit<Meter>(times(kLightYearToMeter))

val Number.meters get() = toDouble().meters
val Number.thou get() = toDouble().thou
val Number.lines get() = toDouble().lines
val Number.inches get() = toDouble().inches
val Number.feet get() = toDouble().feet
val Number.yards get() = toDouble().yards
val Number.miles get() = toDouble().miles
val Number.leagues get() = toDouble().leagues
val Number.nauticalMiles get() = toDouble().nauticalMiles
val Number.lightYears get() = toDouble().lightYears

val SIUnit<Meter>.thou get() = value.div(kThouToMeter)
val SIUnit<Meter>.line get() = value.div(kLineToMeter)
val SIUnit<Meter>.inch get() = value.div(kInchToMeter)
val SIUnit<Meter>.feet get() = value.div(kFeetToMeter)
val SIUnit<Meter>.yard get() = value.div(kYardToMeter)
val SIUnit<Meter>.mile get() = value.div(kMileToMeter)
val SIUnit<Meter>.league get() = value.div(kLeagueToMeter)
val SIUnit<Meter>.nauticalMile get() = value.div(kNauticalMile)
val SIUnit<Meter>.lightYear get() = value.div(kLightYearToMeter)

val SIUnit<Meter>.yottameter get() = value.div(kYotta)
val SIUnit<Meter>.zettameter get() = value.div(kZetta)
val SIUnit<Meter>.exameter get() = value.div(kExa)
val SIUnit<Meter>.petameter get() = value.div(kPeta)
val SIUnit<Meter>.terameter get() = value.div(kTera)
val SIUnit<Meter>.gigameter get() = value.div(kGiga)
val SIUnit<Meter>.megameter get() = value.div(kMega)
val SIUnit<Meter>.kilometer get() = value.div(kKilo)
val SIUnit<Meter>.hectometer get() = value.div(kHecto)
val SIUnit<Meter>.decameter get() = value.div(kDeca)
val SIUnit<Meter>.meter get() = value
val SIUnit<Meter>.decimeter get() = value.div(kDeci)
val SIUnit<Meter>.centimeter get() = value.div(kCenti)
val SIUnit<Meter>.millimeter get() = value.div(kMilli)
val SIUnit<Meter>.micrometer get() = value.div(kMicro)
val SIUnit<Meter>.nanometer get() = value.div(kNano)
val SIUnit<Meter>.picometer get() = value.div(kPico)
val SIUnit<Meter>.femtometer get() = value.div(kFemto)
val SIUnit<Meter>.attometer get() = value.div(kAtto)
val SIUnit<Meter>.zeptometer get() = value.div(kZepto)
val SIUnit<Meter>.yoctometer get() = value.div(kYocto)