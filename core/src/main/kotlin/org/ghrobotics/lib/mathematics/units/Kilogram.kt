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

@Deprecated("Replaced with Plural version", ReplaceWith("lbs"))
val Double.lb get() = lbs
@Deprecated("Replaced with Plural version", ReplaceWith("slugs"))
val Double.slug get() = slugs

@Deprecated("Replaced with Plural version", ReplaceWith("lbs"))
val Number.lb get() = lbs
@Deprecated("Replaced with Plural version", ReplaceWith("slugs"))
val Number.slug get() = slugs

val Double.lbs get() = pounds
val Double.pounds get() = SIUnit<Kilogram>(times(kLbOffsetKilo))
val Double.slugs get() = SIUnit<Kilogram>(times(kSlugOffsetKilo))

val Number.lbs get() = toDouble().lbs
val Number.pounds get() = toDouble().pounds
val Number.slugs get() = toDouble().slugs

val SIUnit<Kilogram>.yottagram get() = value.div(kYottaOffsetKilo)
val SIUnit<Kilogram>.zettagram get() = value.div(kZettaOffsetKilo)
val SIUnit<Kilogram>.exagram get() = value.div(kExaOffsetKilo)
val SIUnit<Kilogram>.petagram get() = value.div(kPetaOffsetKilo)
val SIUnit<Kilogram>.teragram get() = value.div(kTeraOffsetKilo)
val SIUnit<Kilogram>.gigagram get() = value.div(kGigaOffsetKilo)
val SIUnit<Kilogram>.megagram get() = value.div(kMegaOffsetKilo)
val SIUnit<Kilogram>.kilogram get() = value
val SIUnit<Kilogram>.hectogram get() = value.div(kHectoOffsetKilo)
val SIUnit<Kilogram>.decagram get() = value.div(kDecaOffsetKilo)
val SIUnit<Kilogram>.gram get() = value.div(kBaseOffsetKilo)
val SIUnit<Kilogram>.decigram get() = value.div(kDeciOffsetKilo)
val SIUnit<Kilogram>.centigram get() = value.div(kCentiOffsetKilo)
val SIUnit<Kilogram>.milligram get() = value.div(kMilliOffsetKilo)
val SIUnit<Kilogram>.microgram get() = value.div(kMicroOffsetKilo)
val SIUnit<Kilogram>.nanogram get() = value.div(kNanoOffsetKilo)
val SIUnit<Kilogram>.picogram get() = value.div(kPicoOffsetKilo)
val SIUnit<Kilogram>.femtogram get() = value.div(kFemtoOffsetKilo)
val SIUnit<Kilogram>.attogram get() = value.div(kAttoOffsetKilo)
val SIUnit<Kilogram>.zeptogram get() = value.div(kZeptoOffsetKilo)
val SIUnit<Kilogram>.yoctogram get() = value.div(kYoctoOffsetKilo)

val SIUnit<Kilogram>.lb get() = pounds
val SIUnit<Kilogram>.pounds get() = value.div(kLbOffsetKilo)
val SIUnit<Kilogram>.slug get() = value.div(kSlugOffsetKilo)