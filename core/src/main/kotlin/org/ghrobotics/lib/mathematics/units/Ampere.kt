/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

object Ampere : SIKey

val Double.amps get() = SIUnit<Ampere>(this)

val Number.amps get() = toDouble().amps

@Deprecated("Replaced with Plural version", ReplaceWith("inYottaamps()"))
val SIUnit<Ampere>.yottaamp
    get() = inYottaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inZettaamps()"))
val SIUnit<Ampere>.zettaamp
    get() = inZettaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inExaamps()"))
val SIUnit<Ampere>.exaamp
    get() = inExaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inPetaamps()"))
val SIUnit<Ampere>.petaamp
    get() = inPetaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inTeraamps()"))
val SIUnit<Ampere>.teraamp
    get() = inTeraamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inGigaamps()"))
val SIUnit<Ampere>.gigaamp
    get() = inGigaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inMegaamps()"))
val SIUnit<Ampere>.megaamp
    get() = inMegaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inKiloamps()"))
val SIUnit<Ampere>.kiloamp
    get() = inKiloamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inHectoamps()"))
val SIUnit<Ampere>.hectoamp
    get() = inHectoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inDecaamps()"))
val SIUnit<Ampere>.decaamp
    get() = inDecaamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inAmps()"))
val SIUnit<Ampere>.amp
    get() = inAmps()

@Deprecated("Replaced with Plural version", ReplaceWith("inDeciamps()"))
val SIUnit<Ampere>.deciamp
    get() = inDeciamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inCentiamps()"))
val SIUnit<Ampere>.centiamp
    get() = inCentiamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inMilliamps()"))
val SIUnit<Ampere>.milliamp
    get() = inMilliamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inMicroamps()"))
val SIUnit<Ampere>.microamp
    get() = inMicroamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inNanoamps()"))
val SIUnit<Ampere>.nanoamp
    get() = inNanoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inPicoamps()"))
val SIUnit<Ampere>.picoamp
    get() = inPicoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inFemtoamps()"))
val SIUnit<Ampere>.femtoamp
    get() = inFemtoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inAttoamps()"))
val SIUnit<Ampere>.attoamp
    get() = inAttoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inZeptoamps()"))
val SIUnit<Ampere>.zeptoamp
    get() = inZeptoamps()

@Deprecated("Replaced with Plural version", ReplaceWith("inYoctoamps()"))
val SIUnit<Ampere>.yoctoamp
    get() = inYoctoamps()

fun SIUnit<Ampere>.inYottaamps() = value.div(kYotta)
fun SIUnit<Ampere>.inZettaamps() = value.div(kZetta)
fun SIUnit<Ampere>.inExaamps() = value.div(kExa)
fun SIUnit<Ampere>.inPetaamps() = value.div(kPeta)
fun SIUnit<Ampere>.inTeraamps() = value.div(kTera)
fun SIUnit<Ampere>.inGigaamps() = value.div(kGiga)
fun SIUnit<Ampere>.inMegaamps() = value.div(kMega)
fun SIUnit<Ampere>.inKiloamps() = value.div(kKilo)
fun SIUnit<Ampere>.inHectoamps() = value.div(kHecto)
fun SIUnit<Ampere>.inDecaamps() = value.div(kDeca)
fun SIUnit<Ampere>.inAmps() = value
fun SIUnit<Ampere>.inDeciamps() = value.div(kDeci)
fun SIUnit<Ampere>.inCentiamps() = value.div(kCenti)
fun SIUnit<Ampere>.inMilliamps() = value.div(kMilli)
fun SIUnit<Ampere>.inMicroamps() = value.div(kMicro)
fun SIUnit<Ampere>.inNanoamps() = value.div(kNano)
fun SIUnit<Ampere>.inPicoamps() = value.div(kPico)
fun SIUnit<Ampere>.inFemtoamps() = value.div(kFemto)
fun SIUnit<Ampere>.inAttoamps() = value.div(kAtto)
fun SIUnit<Ampere>.inZeptoamps() = value.div(kZepto)
fun SIUnit<Ampere>.inYoctoamps() = value.div(kYocto)
