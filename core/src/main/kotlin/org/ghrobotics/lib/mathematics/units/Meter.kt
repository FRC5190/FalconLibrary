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
const val kFeetToMeter = kInchToMeter * 12
const val kYardToMeter = kFeetToMeter * 3
const val kMileToMeter = kFeetToMeter * 5280
const val kLeagueToMeter = kMileToMeter * 3
const val kNauticalMile = 1852
const val kLightYearToMeter = 9460730472580800.0

val Double.meter get() = SIUnit<Meter>(this)
val Double.thou get() = SIUnit<Meter>(times(kThouToMeter))
val Double.line get() = SIUnit<Meter>(times(kLineToMeter))
val Double.inch get() = SIUnit<Meter>(times(kInchToMeter))
val Double.feet get() = SIUnit<Meter>(times(kFeetToMeter))
val Double.yard get() = SIUnit<Meter>(times(kYardToMeter))
val Double.mile get() = SIUnit<Meter>(times(kMileToMeter))
val Double.league get() = SIUnit<Meter>(times(kLeagueToMeter))
val Double.nauticalMile get() = SIUnit<Meter>(times(kNauticalMile))
val Double.lightYear get() = SIUnit<Meter>(times(kLightYearToMeter))

val Number.meter get() = toDouble().meter
val Number.thou get() = toDouble().thou
val Number.line get() = toDouble().line
val Number.inch get() = toDouble().inch
val Number.feet get() = toDouble().feet
val Number.yard get() = toDouble().yard
val Number.mile get() = toDouble().mile
val Number.league get() = toDouble().league
val Number.nauticalMile get() = toDouble().nauticalMile
val Number.lightYear get() = toDouble().lightYear

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