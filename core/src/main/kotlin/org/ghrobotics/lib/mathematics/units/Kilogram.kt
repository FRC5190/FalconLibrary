/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

object Kilogram : SIKey

const val kYottaOffsetKilo = 1e21
const val kZettaOffsetKilo = 1e18
const val kExaOffsetKilo = 1e15
const val kPetaOffsetKilo = 1e12
const val kTeraOffsetKilo = 1e9
const val kGigaOffsetKilo = 1e6
const val kMegaOffsetKilo = 1e3
const val kHectoOffsetKilo = 1e-1
const val kDecaOffsetKilo = 1e-2
const val kBaseOffsetKilo = 1e-3
const val kDeciOffsetKilo = 1e-4
const val kCentiOffsetKilo = 1e-5
const val kMilliOffsetKilo = 1e-6
const val kMicroOffsetKilo = 1e-9
const val kNanoOffsetKilo = 1e-12
const val kPicoOffsetKilo = 1e-15
const val kFemtoOffsetKilo = 1e-18
const val kAttoOffsetKilo = 1e-21
const val kZeptoOffsetKilo = 1e-24
const val kYoctoOffsetKilo = 1e-27

const val kLbOffsetKilo = 0.453592
const val kSlugOffsetKilo = 14.59

val Double.lbs get() = pounds
val Double.pounds get() = SIUnit<Kilogram>(times(kLbOffsetKilo))
val Double.slugs get() = SIUnit<Kilogram>(times(kSlugOffsetKilo))

val Number.lbs get() = toDouble().lbs
val Number.pounds get() = toDouble().pounds
val Number.slugs get() = toDouble().slugs

@Deprecated("Replaced with Plural version", ReplaceWith("inYottagrams()"))
val SIUnit<Kilogram>.yottagram
    get() = inYottagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inZettagrams()"))
val SIUnit<Kilogram>.zettagram
    get() = inZettagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inExagrams()"))
val SIUnit<Kilogram>.exagram
    get() = inExagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inPetagrams()"))
val SIUnit<Kilogram>.petagram
    get() = inPetagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inTeragrams()"))
val SIUnit<Kilogram>.teragram
    get() = inTeragrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inGigagrams()"))
val SIUnit<Kilogram>.gigagram
    get() = inGigagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inMegagrams()"))
val SIUnit<Kilogram>.megagram
    get() = inMegagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inKilograms()"))
val SIUnit<Kilogram>.kilogram
    get() = inKilograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inHectograms()"))
val SIUnit<Kilogram>.hectogram
    get() = inHectograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inDecagrams()"))
val SIUnit<Kilogram>.decagram
    get() = inDecagrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inGrams()"))
val SIUnit<Kilogram>.gram
    get() = inGrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inDecigrams()"))
val SIUnit<Kilogram>.decigram
    get() = inDecigrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inCentigrams()"))
val SIUnit<Kilogram>.centigram
    get() = inCentigrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inMilligrams()"))
val SIUnit<Kilogram>.milligram
    get() = inMilligrams()

@Deprecated("Replaced with Plural version", ReplaceWith("inMicrograms()"))
val SIUnit<Kilogram>.microgram
    get() = inMicrograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inNanograms()"))
val SIUnit<Kilogram>.nanogram
    get() = inNanograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inPicograms()"))
val SIUnit<Kilogram>.picogram
    get() = inPicograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inFemtograms()"))
val SIUnit<Kilogram>.femtogram
    get() = inFemtograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inAttograms()"))
val SIUnit<Kilogram>.attogram
    get() = inAttograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inZeptograms()"))
val SIUnit<Kilogram>.zeptogram
    get() = inZeptograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inYoctograms()"))
val SIUnit<Kilogram>.yoctogram
    get() = inYoctograms()

@Deprecated("Replaced with Plural version", ReplaceWith("inLbs()"))
val SIUnit<Kilogram>.lb
    get() = inLbs()

@Deprecated("Replaced with Plural version", ReplaceWith("inPounds()"))
val SIUnit<Kilogram>.pounds
    get() = inPounds()

@Deprecated("Replaced with Plural version", ReplaceWith("inSlugs()"))
val SIUnit<Kilogram>.slug
    get() = inSlugs()

fun SIUnit<Kilogram>.inYottagrams() = value.div(kYottaOffsetKilo)
fun SIUnit<Kilogram>.inZettagrams() = value.div(kZettaOffsetKilo)
fun SIUnit<Kilogram>.inExagrams() = value.div(kExaOffsetKilo)
fun SIUnit<Kilogram>.inPetagrams() = value.div(kPetaOffsetKilo)
fun SIUnit<Kilogram>.inTeragrams() = value.div(kTeraOffsetKilo)
fun SIUnit<Kilogram>.inGigagrams() = value.div(kGigaOffsetKilo)
fun SIUnit<Kilogram>.inMegagrams() = value.div(kMegaOffsetKilo)
fun SIUnit<Kilogram>.inKilograms() = value
fun SIUnit<Kilogram>.inHectograms() = value.div(kHectoOffsetKilo)
fun SIUnit<Kilogram>.inDecagrams() = value.div(kDecaOffsetKilo)
fun SIUnit<Kilogram>.inGrams() = value.div(kBaseOffsetKilo)
fun SIUnit<Kilogram>.inDecigrams() = value.div(kDeciOffsetKilo)
fun SIUnit<Kilogram>.inCentigrams() = value.div(kCentiOffsetKilo)
fun SIUnit<Kilogram>.inMilligrams() = value.div(kMilliOffsetKilo)
fun SIUnit<Kilogram>.inMicrograms() = value.div(kMicroOffsetKilo)
fun SIUnit<Kilogram>.inNanograms() = value.div(kNanoOffsetKilo)
fun SIUnit<Kilogram>.inPicograms() = value.div(kPicoOffsetKilo)
fun SIUnit<Kilogram>.inFemtograms() = value.div(kFemtoOffsetKilo)
fun SIUnit<Kilogram>.inAttograms() = value.div(kAttoOffsetKilo)
fun SIUnit<Kilogram>.inZeptograms() = value.div(kZeptoOffsetKilo)
fun SIUnit<Kilogram>.inYoctograms() = value.div(kYoctoOffsetKilo)

fun SIUnit<Kilogram>.inLbs() = inPounds()
fun SIUnit<Kilogram>.inPounds() = value.div(kLbOffsetKilo)
fun SIUnit<Kilogram>.inSlugs() = value.div(kSlugOffsetKilo)
