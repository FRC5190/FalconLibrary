package org.ghrobotics.lib.mathematics.units2

import org.ghrobotics.lib.mathematics.units2.derived.Ohm
import org.ghrobotics.lib.mathematics.units2.derived.Volt
import org.ghrobotics.lib.mathematics.units2.derived.Watt

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