package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.ElectricCurrent
import org.ghrobotics.lib.mathematics.units.SIConstants
import org.ghrobotics.lib.mathematics.units.SIValue

val Number.volt get() = Volt(toDouble())

val Number.yottavolt get() = Volt(toDouble() * SIConstants.kYotta)
val Number.zettavolt get() = Volt(toDouble() * SIConstants.kZetta)
val Number.exavolt get() = Volt(toDouble() * SIConstants.kExa)
val Number.petavolt get() = Volt(toDouble() * SIConstants.kPeta)
val Number.teravolt get() = Volt(toDouble() * SIConstants.kTera)
val Number.gigavolt get() = Volt(toDouble() * SIConstants.kGiga)
val Number.megavolt get() = Volt(toDouble() * SIConstants.kMega)
val Number.kilovolt get() = Volt(toDouble() * SIConstants.kKilo)
val Number.hectovolt get() = Volt(toDouble() * SIConstants.kHecto)
val Number.decavolt get() = Volt(toDouble() * SIConstants.kDeca)
val Number.decivolt get() = Volt(toDouble() * SIConstants.kDeci)
val Number.centivolt get() = Volt(toDouble() * SIConstants.kCenti)
val Number.millivolt get() = Volt(toDouble() * SIConstants.kMilli)
val Number.microvolt get() = Volt(toDouble() * SIConstants.kMicro)
val Number.nanovolt get() = Volt(toDouble() * SIConstants.kNano)
val Number.picovolt get() = Volt(toDouble() * SIConstants.kPico)
val Number.femtovolt get() = Volt(toDouble() * SIConstants.kFemto)
val Number.attovolt get() = Volt(toDouble() * SIConstants.kAtto)
val Number.zeptovolt get() = Volt(toDouble() * SIConstants.kZepto)
val Number.yoctovolt get() = Volt(toDouble() * SIConstants.kYocto)

class Volt(
    override val value: Double
) : SIValue<Volt> {
    override fun createNew(newValue: Double) = Volt(value)

    operator fun times(other: ElectricCurrent) = Watt(value * other.value)
    operator fun div(other: ElectricCurrent) = Ohm(value / other.value)

    companion object {
        val kZero = Volt(0.0)
    }
}