package org.ghrobotics.lib.mathematics.units

import org.ghrobotics.lib.mathematics.units.derived.Ohm
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.Watt

inline class SIUnitBuilder(private val value: Double) {
    val second get() = SIUnit<Second>(value)
    val meter get() = SIUnit<Meter>(value)
    val gram get() = SIUnit<Kilogram>(value.times(kBaseOffsetKilo))
    val amp get() = SIUnit<Ampere>(value)
    val ohm get() = SIUnit<Ohm>(value)
    val volt get() = SIUnit<Volt>(value)
    val watt get() = SIUnit<Watt>(value)
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