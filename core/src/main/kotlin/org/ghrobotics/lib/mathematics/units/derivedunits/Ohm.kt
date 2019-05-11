package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.SIConstants
import org.ghrobotics.lib.mathematics.units.SIValue

val Number.ohm get() = Ohm(toDouble())

val Number.yottaohm get() = Ohm(toDouble() * SIConstants.kYotta)
val Number.zettaohm get() = Ohm(toDouble() * SIConstants.kZetta)
val Number.exaohm get() = Ohm(toDouble() * SIConstants.kExa)
val Number.petaohm get() = Ohm(toDouble() * SIConstants.kPeta)
val Number.teraohm get() = Ohm(toDouble() * SIConstants.kTera)
val Number.gigaohm get() = Ohm(toDouble() * SIConstants.kGiga)
val Number.megaohm get() = Ohm(toDouble() * SIConstants.kMega)
val Number.kiloohm get() = Ohm(toDouble() * SIConstants.kKilo)
val Number.hectoohm get() = Ohm(toDouble() * SIConstants.kHecto)
val Number.decaohm get() = Ohm(toDouble() * SIConstants.kDeca)
val Number.deciohm get() = Ohm(toDouble() * SIConstants.kDeci)
val Number.centiohm get() = Ohm(toDouble() * SIConstants.kCenti)
val Number.milliohm get() = Ohm(toDouble() * SIConstants.kMilli)
val Number.microohm get() = Ohm(toDouble() * SIConstants.kMicro)
val Number.nanoohm get() = Ohm(toDouble() * SIConstants.kNano)
val Number.picoohm get() = Ohm(toDouble() * SIConstants.kPico)
val Number.femtoohm get() = Ohm(toDouble() * SIConstants.kFemto)
val Number.attoohm get() = Ohm(toDouble() * SIConstants.kAtto)
val Number.zeptoohm get() = Ohm(toDouble() * SIConstants.kZepto)
val Number.yoctoohm get() = Ohm(toDouble() * SIConstants.kYocto)

class Ohm(
    override val value: Double
) : SIValue<Ohm> {
    override fun createNew(newValue: Double) = Ohm(value)

    companion object {
        val kZero = Ohm(0.0)
    }
}