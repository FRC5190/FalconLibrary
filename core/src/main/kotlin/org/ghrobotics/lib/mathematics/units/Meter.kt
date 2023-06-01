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

@Deprecated("Replaced with Plural version", ReplaceWith("inThous()"))
val SIUnit<Meter>.thou
    get() = inThou()

@Deprecated("Replaced with Plural version", ReplaceWith("inLines()"))
val SIUnit<Meter>.line
    get() = inLines()

@Deprecated("Replaced with Plural version", ReplaceWith("inInchs()"))
val SIUnit<Meter>.inch
    get() = inInches()

@Deprecated("Replaced with Plural version", ReplaceWith("inFeets()"))
val SIUnit<Meter>.feet
    get() = inFeet()

@Deprecated("Replaced with Plural version", ReplaceWith("inYards()"))
val SIUnit<Meter>.yard
    get() = inYards()

@Deprecated("Replaced with Plural version", ReplaceWith("inMiles()"))
val SIUnit<Meter>.mile
    get() = inMiles()

@Deprecated("Replaced with Plural version", ReplaceWith("inLeagues()"))
val SIUnit<Meter>.league
    get() = inLeagues()

@Deprecated("Replaced with Plural version", ReplaceWith("inNauticalMiles()"))
val SIUnit<Meter>.nauticalMile
    get() = inNauticalMiles()

@Deprecated("Replaced with Plural version", ReplaceWith("inLightYears()"))
val SIUnit<Meter>.lightYear
    get() = inLightYears()

@Deprecated("Replaced with Plural version", ReplaceWith("inYottameters()"))
val SIUnit<Meter>.yottameter
    get() = inYottameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inZettameters()"))
val SIUnit<Meter>.zettameter
    get() = inZettameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inExameters()"))
val SIUnit<Meter>.exameter
    get() = inExameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inPetameters()"))
val SIUnit<Meter>.petameter
    get() = inPetameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inTerameters()"))
val SIUnit<Meter>.terameter
    get() = inTerameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inGigameters()"))
val SIUnit<Meter>.gigameter
    get() = inGigameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inMegameters()"))
val SIUnit<Meter>.megameter
    get() = inMegameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inKilometers()"))
val SIUnit<Meter>.kilometer
    get() = inKilometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inHectometers()"))
val SIUnit<Meter>.hectometer
    get() = inHectometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inDecameters()"))
val SIUnit<Meter>.decameter
    get() = inDecameters()

@Deprecated("Replaced with Plural version", ReplaceWith("inMeters()"))
val SIUnit<Meter>.meter
    get() = inMeters()

@Deprecated("Replaced with Plural version", ReplaceWith("inDecimeters()"))
val SIUnit<Meter>.decimeter
    get() = inDecimeters()

@Deprecated("Replaced with Plural version", ReplaceWith("inCentimeters()"))
val SIUnit<Meter>.centimeter
    get() = inCentimeters()

@Deprecated("Replaced with Plural version", ReplaceWith("inMillimeters()"))
val SIUnit<Meter>.millimeter
    get() = inMillimeters()

@Deprecated("Replaced with Plural version", ReplaceWith("inMicrometers()"))
val SIUnit<Meter>.micrometer
    get() = inMicrometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inNanometers()"))
val SIUnit<Meter>.nanometer
    get() = inNanometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inPicometers()"))
val SIUnit<Meter>.picometer
    get() = inPicometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inFemtometers()"))
val SIUnit<Meter>.femtometer
    get() = inFemtometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inAttometers()"))
val SIUnit<Meter>.attometer
    get() = inAttometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inZeptometers()"))
val SIUnit<Meter>.zeptometer
    get() = inZeptometers()

@Deprecated("Replaced with Plural version", ReplaceWith("inYoctometers()"))
val SIUnit<Meter>.yoctometer
    get() = inYoctometers()

fun SIUnit<Meter>.inThou() = value.div(kThouToMeter)
fun SIUnit<Meter>.inLines() = value.div(kLineToMeter)
fun SIUnit<Meter>.inInches() = value.div(kInchToMeter)
fun SIUnit<Meter>.inFeet() = value.div(kFeetToMeter)
fun SIUnit<Meter>.inYards() = value.div(kYardToMeter)
fun SIUnit<Meter>.inMiles() = value.div(kMileToMeter)
fun SIUnit<Meter>.inLeagues() = value.div(kLeagueToMeter)
fun SIUnit<Meter>.inNauticalMiles() = value.div(kNauticalMile)
fun SIUnit<Meter>.inLightYears() = value.div(kLightYearToMeter)

fun SIUnit<Meter>.inYottameters() = value.div(kYotta)
fun SIUnit<Meter>.inZettameters() = value.div(kZetta)
fun SIUnit<Meter>.inExameters() = value.div(kExa)
fun SIUnit<Meter>.inPetameters() = value.div(kPeta)
fun SIUnit<Meter>.inTerameters() = value.div(kTera)
fun SIUnit<Meter>.inGigameters() = value.div(kGiga)
fun SIUnit<Meter>.inMegameters() = value.div(kMega)
fun SIUnit<Meter>.inKilometers() = value.div(kKilo)
fun SIUnit<Meter>.inHectometers() = value.div(kHecto)
fun SIUnit<Meter>.inDecameters() = value.div(kDeca)
fun SIUnit<Meter>.inMeters() = value
fun SIUnit<Meter>.inDecimeters() = value.div(kDeci)
fun SIUnit<Meter>.inCentimeters() = value.div(kCenti)
fun SIUnit<Meter>.inMillimeters() = value.div(kMilli)
fun SIUnit<Meter>.inMicrometers() = value.div(kMicro)
fun SIUnit<Meter>.inNanometers() = value.div(kNano)
fun SIUnit<Meter>.inPicometers() = value.div(kPico)
fun SIUnit<Meter>.inFemtometers() = value.div(kFemto)
fun SIUnit<Meter>.inAttometers() = value.div(kAtto)
fun SIUnit<Meter>.inZeptometers() = value.div(kZepto)
fun SIUnit<Meter>.inYoctometers() = value.div(kYocto)
