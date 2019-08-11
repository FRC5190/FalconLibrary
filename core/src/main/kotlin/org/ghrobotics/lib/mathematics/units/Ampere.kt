/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

object Ampere : SIKey

@Deprecated("Replaced with Plural version", ReplaceWith("amps"))
val Double.amp get() = amps

@Deprecated("Replaced with Plural version", ReplaceWith("amps"))
val Number.amp get() = amps

val Double.amps get() = SIUnit<Ampere>(this)

val Number.amps get() = toDouble().amps

val SIUnit<Ampere>.yottaamp get() = value.div(kYotta)
val SIUnit<Ampere>.zettaamp get() = value.div(kZetta)
val SIUnit<Ampere>.exaamp get() = value.div(kExa)
val SIUnit<Ampere>.petaamp get() = value.div(kPeta)
val SIUnit<Ampere>.teraamp get() = value.div(kTera)
val SIUnit<Ampere>.gigaamp get() = value.div(kGiga)
val SIUnit<Ampere>.megaamp get() = value.div(kMega)
val SIUnit<Ampere>.kiloamp get() = value.div(kKilo)
val SIUnit<Ampere>.hectoamp get() = value.div(kHecto)
val SIUnit<Ampere>.decaamp get() = value.div(kDeca)
val SIUnit<Ampere>.amp get() = value
val SIUnit<Ampere>.deciamp get() = value.div(kDeci)
val SIUnit<Ampere>.centiamp get() = value.div(kCenti)
val SIUnit<Ampere>.milliamp get() = value.div(kMilli)
val SIUnit<Ampere>.microamp get() = value.div(kMicro)
val SIUnit<Ampere>.nanoamp get() = value.div(kNano)
val SIUnit<Ampere>.picoamp get() = value.div(kPico)
val SIUnit<Ampere>.femtoamp get() = value.div(kFemto)
val SIUnit<Ampere>.attoamp get() = value.div(kAtto)
val SIUnit<Ampere>.zeptoamp get() = value.div(kZepto)
val SIUnit<Ampere>.yoctoamp get() = value.div(kYocto)