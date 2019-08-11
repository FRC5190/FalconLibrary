/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.units.derived.Ohm
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.Watt

inline class SIUnitBuilder(private val value: Double) {
    @Deprecated("Replaced with Plural version", ReplaceWith("seconds"))
    val second get() = seconds
    @Deprecated("Replaced with Plural version", ReplaceWith("meters"))
    val meter get() = meters
    @Deprecated("Replaced with Plural version", ReplaceWith("grams"))
    val gram get() = grams
    @Deprecated("Replaced with Plural version", ReplaceWith("amps"))
    val amp get() = amps
    @Deprecated("Replaced with Plural version", ReplaceWith("ohms"))
    val ohm get() = ohms
    @Deprecated("Replaced with Plural version", ReplaceWith("volts"))
    val volt get() = volts
    @Deprecated("Replaced with Plural version", ReplaceWith("watts"))
    val watt get() = watts

    val seconds get() = SIUnit<Second>(value)
    val meters get() = SIUnit<Meter>(value)
    val grams get() = SIUnit<Kilogram>(value.times(kBaseOffsetKilo))
    val amps get() = SIUnit<Ampere>(value)
    val ohms get() = SIUnit<Ohm>(value)
    val volts get() = SIUnit<Volt>(value)
    val watts get() = SIUnit<Watt>(value)
}

val Double.yotta get() = SIUnitBuilder(times(kYotta))
val Double.zetta get() = SIUnitBuilder(times(kZetta))
val Double.exa get() = SIUnitBuilder(times(kExa))
val Double.peta get() = SIUnitBuilder(times(kPeta))
val Double.tera get() = SIUnitBuilder(times(kTera))
val Double.giga get() = SIUnitBuilder(times(kGiga))
val Double.mega get() = SIUnitBuilder(times(kMega))
val Double.kilo get() = SIUnitBuilder(times(kKilo))
val Double.hecto get() = SIUnitBuilder(times(kHecto))
val Double.deca get() = SIUnitBuilder(times(kDeca))
val Double.base get() = SIUnitBuilder(this)
val Double.deci get() = SIUnitBuilder(times(kDeci))
val Double.centi get() = SIUnitBuilder(times(kCenti))
val Double.milli get() = SIUnitBuilder(times(kMilli))
val Double.micro get() = SIUnitBuilder(times(kMicro))
val Double.nano get() = SIUnitBuilder(times(kNano))
val Double.pico get() = SIUnitBuilder(times(kPico))
val Double.femto get() = SIUnitBuilder(times(kFemto))
val Double.atto get() = SIUnitBuilder(times(kAtto))
val Double.zepto get() = SIUnitBuilder(times(kZepto))
val Double.yocto get() = SIUnitBuilder(times(kYocto))

val Number.yotta get() = toDouble().yotta
val Number.zetta get() = toDouble().zetta
val Number.exa get() = toDouble().exa
val Number.peta get() = toDouble().peta
val Number.tera get() = toDouble().tera
val Number.giga get() = toDouble().giga
val Number.mega get() = toDouble().mega
val Number.kilo get() = toDouble().kilo
val Number.hecto get() = toDouble().hecto
val Number.deca get() = toDouble().deca
val Number.base get() = toDouble().base
val Number.deci get() = toDouble().deci
val Number.centi get() = toDouble().centi
val Number.milli get() = toDouble().milli
val Number.micro get() = toDouble().micro
val Number.nano get() = toDouble().nano
val Number.pico get() = toDouble().pico
val Number.femto get() = toDouble().femto
val Number.atto get() = toDouble().atto
val Number.zepto get() = toDouble().zepto
val Number.yocto get() = toDouble().yocto