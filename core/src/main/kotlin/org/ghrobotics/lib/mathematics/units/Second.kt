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

@Deprecated("Replaced with Plural version", ReplaceWith("seconds"))
val Double.second get() = seconds
@Deprecated("Replaced with Plural version", ReplaceWith("minutes"))
val Double.minute get() = minutes
@Deprecated("Replaced with Plural version", ReplaceWith("hours"))
val Double.hour get() = hours
@Deprecated("Replaced with Plural version", ReplaceWith("days"))
val Double.day get() = days
@Deprecated("Replaced with Plural version", ReplaceWith("weeks"))
val Double.week get() = weeks
@Deprecated("Replaced with Plural version", ReplaceWith("moments"))
val Double.moment get() = moments

@Deprecated("Replaced with Plural version", ReplaceWith("seconds"))
val Number.second get() = seconds
@Deprecated("Replaced with Plural version", ReplaceWith("minutes"))
val Number.minute get() = minutes
@Deprecated("Replaced with Plural version", ReplaceWith("hours"))
val Number.hour get() = hours
@Deprecated("Replaced with Plural version", ReplaceWith("days"))
val Number.day get() = days
@Deprecated("Replaced with Plural version", ReplaceWith("weeks"))
val Number.week get() = weeks
@Deprecated("Replaced with Plural version", ReplaceWith("moments"))
val Number.moment get() = moments

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

val SIUnit<Second>.minute get() = value.div(kMinuteToSecond)
val SIUnit<Second>.hour get() = value.div(kHourToSecond)
val SIUnit<Second>.day get() = value.div(kDayToSecond)
val SIUnit<Second>.week get() = value.div(kWeekToSecond)
val SIUnit<Second>.moment get() = value.div(kMomentToSecond)

val SIUnit<Second>.yottasecond get() = value.div(kYotta)
val SIUnit<Second>.zettasecond get() = value.div(kZetta)
val SIUnit<Second>.exasecond get() = value.div(kExa)
val SIUnit<Second>.petasecond get() = value.div(kPeta)
val SIUnit<Second>.terasecond get() = value.div(kTera)
val SIUnit<Second>.gigasecond get() = value.div(kGiga)
val SIUnit<Second>.megasecond get() = value.div(kMega)
val SIUnit<Second>.kilosecond get() = value.div(kKilo)
val SIUnit<Second>.hectosecond get() = value.div(kHecto)
val SIUnit<Second>.decasecond get() = value.div(kDeca)
val SIUnit<Second>.second get() = value
val SIUnit<Second>.decisecond get() = value.div(kDeci)
val SIUnit<Second>.centisecond get() = value.div(kCenti)
val SIUnit<Second>.millisecond get() = value.div(kMilli)
val SIUnit<Second>.microsecond get() = value.div(kMicro)
val SIUnit<Second>.nanosecond get() = value.div(kNano)
val SIUnit<Second>.picosecond get() = value.div(kPico)
val SIUnit<Second>.femtosecond get() = value.div(kFemto)
val SIUnit<Second>.attosecond get() = value.div(kAtto)
val SIUnit<Second>.zeptosecond get() = value.div(kZepto)
val SIUnit<Second>.yoctosecond get() = value.div(kYocto)