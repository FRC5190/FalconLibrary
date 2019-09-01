/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

object Second : SIKey

const val kMinuteToSecond = 60.0
const val kHourToSecond = kMinuteToSecond * 60.0
const val kDayToSecond = kHourToSecond * 24.0
const val kWeekToSecond = kDayToSecond * 7.0
const val kMomentToSecond = 90.0

val Double.seconds get() = SIUnit<Second>(this)
val Double.minutes get() = SIUnit<Second>(times(kMinuteToSecond))
val Double.hours get() = SIUnit<Second>(times(kHourToSecond))
val Double.days get() = SIUnit<Second>(times(kDayToSecond))
val Double.weeks get() = SIUnit<Second>(times(kWeekToSecond))
val Double.moments get() = SIUnit<Second>(times(kMomentToSecond))

val Number.seconds get() = toDouble().seconds
val Number.minutes get() = toDouble().minutes
val Number.hours get() = toDouble().hours
val Number.days get() = toDouble().days
val Number.weeks get() = toDouble().weeks
val Number.moments get() = toDouble().moments

@Deprecated("Replaced with Plural version", ReplaceWith("inMinutes()"))
val SIUnit<Second>.minute get() = inMinutes()
@Deprecated("Replaced with Plural version", ReplaceWith("inMinutes()"))
val SIUnit<Second>.hour get() = inHours()
@Deprecated("Replaced with Plural version", ReplaceWith("inMinutes()"))
val SIUnit<Second>.day get() = inDays()
@Deprecated("Replaced with Plural version", ReplaceWith("inMinutes()"))
val SIUnit<Second>.week get() = inWeeks()
@Deprecated("Replaced with Plural version", ReplaceWith("inMinutes()"))
val SIUnit<Second>.moment get() = inMoments()

@Deprecated("Replaced with Plural version", ReplaceWith("inYottaseconds()"))
val SIUnit<Second>.yottasecond get() = inYottaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inZettaseconds()"))
val SIUnit<Second>.zettasecond get() = inZettaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inExaseconds()"))
val SIUnit<Second>.exasecond get() = inExaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inPetaseconds()"))
val SIUnit<Second>.petasecond get() = inPetaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inTeraseconds()"))
val SIUnit<Second>.terasecond get() = inTeraseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inGigaseconds()"))
val SIUnit<Second>.gigasecond get() = inGigaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inMegaseconds()"))
val SIUnit<Second>.megasecond get() = inMegaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inKiloseconds()"))
val SIUnit<Second>.kilosecond get() = inKiloseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inHectoseconds()"))
val SIUnit<Second>.hectosecond get() = inHectoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inDecaseconds()"))
val SIUnit<Second>.decasecond get() = inDecaseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inSeconds()"))
val SIUnit<Second>.second get() = inSeconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inDeciseconds()"))
val SIUnit<Second>.decisecond get() = inDeciseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inCentiseconds()"))
val SIUnit<Second>.centisecond get() = inCentiseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inMilliseconds()"))
val SIUnit<Second>.millisecond get() = inMilliseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inMicroseconds()"))
val SIUnit<Second>.microsecond get() = inMicroseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inNanoseconds()"))
val SIUnit<Second>.nanosecond get() = inNanoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inPicoseconds()"))
val SIUnit<Second>.picosecond get() = inPicoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inFemtoseconds()"))
val SIUnit<Second>.femtosecond get() = inFemtoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inAttoseconds()"))
val SIUnit<Second>.attosecond get() = inAttoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inZeptoseconds()"))
val SIUnit<Second>.zeptosecond get() = inZeptoseconds()
@Deprecated("Replaced with Plural version", ReplaceWith("inYoctoseconds()"))
val SIUnit<Second>.yoctosecond get() = inYoctoseconds()

fun SIUnit<Second>.inMinutes() = value.div(kMinuteToSecond)
fun SIUnit<Second>.inHours() = value.div(kHourToSecond)
fun SIUnit<Second>.inDays() = value.div(kDayToSecond)
fun SIUnit<Second>.inWeeks() = value.div(kWeekToSecond)
fun SIUnit<Second>.inMoments() = value.div(kMomentToSecond)

fun SIUnit<Second>.inYottaseconds() = value.div(kYotta)
fun SIUnit<Second>.inZettaseconds() = value.div(kZetta)
fun SIUnit<Second>.inExaseconds() = value.div(kExa)
fun SIUnit<Second>.inPetaseconds() = value.div(kPeta)
fun SIUnit<Second>.inTeraseconds() = value.div(kTera)
fun SIUnit<Second>.inGigaseconds() = value.div(kGiga)
fun SIUnit<Second>.inMegaseconds() = value.div(kMega)
fun SIUnit<Second>.inKiloseconds() = value.div(kKilo)
fun SIUnit<Second>.inHectoseconds() = value.div(kHecto)
fun SIUnit<Second>.inDecaseconds() = value.div(kDeca)
fun SIUnit<Second>.inSeconds() = value
fun SIUnit<Second>.inDeciseconds() = value.div(kDeci)
fun SIUnit<Second>.inCentiseconds() = value.div(kCenti)
fun SIUnit<Second>.inMilliseconds() = value.div(kMilli)
fun SIUnit<Second>.inMicroseconds() = value.div(kMicro)
fun SIUnit<Second>.inNanoseconds() = value.div(kNano)
fun SIUnit<Second>.inPicoseconds() = value.div(kPico)
fun SIUnit<Second>.inFemtoseconds() = value.div(kFemto)
fun SIUnit<Second>.inAttoseconds() = value.div(kAtto)
fun SIUnit<Second>.inZeptoseconds() = value.div(kZepto)
fun SIUnit<Second>.inYoctoseconds() = value.div(kYocto)