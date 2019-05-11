package org.ghrobotics.lib.mathematics.units.derivedunits

import org.ghrobotics.lib.mathematics.units.SIConstants
import org.ghrobotics.lib.mathematics.units.SIValue

val Number.watt get() = Watt(toDouble())

val Number.yottawatt get() = Watt(toDouble() * SIConstants.kYotta)
val Number.zettawatt get() = Watt(toDouble() * SIConstants.kZetta)
val Number.exawatt get() = Watt(toDouble() * SIConstants.kExa)
val Number.petawatt get() = Watt(toDouble() * SIConstants.kPeta)
val Number.terawatt get() = Watt(toDouble() * SIConstants.kTera)
val Number.gigawatt get() = Watt(toDouble() * SIConstants.kGiga)
val Number.megawatt get() = Watt(toDouble() * SIConstants.kMega)
val Number.kilowatt get() = Watt(toDouble() * SIConstants.kKilo)
val Number.hectowatt get() = Watt(toDouble() * SIConstants.kHecto)
val Number.decawatt get() = Watt(toDouble() * SIConstants.kDeca)
val Number.deciwatt get() = Watt(toDouble() * SIConstants.kDeci)
val Number.centiwatt get() = Watt(toDouble() * SIConstants.kCenti)
val Number.milliwatt get() = Watt(toDouble() * SIConstants.kMilli)
val Number.microwatt get() = Watt(toDouble() * SIConstants.kMicro)
val Number.nanowatt get() = Watt(toDouble() * SIConstants.kNano)
val Number.picowatt get() = Watt(toDouble() * SIConstants.kPico)
val Number.femtowatt get() = Watt(toDouble() * SIConstants.kFemto)
val Number.attowatt get() = Watt(toDouble() * SIConstants.kAtto)
val Number.zeptowatt get() = Watt(toDouble() * SIConstants.kZepto)
val Number.yoctowatt get() = Watt(toDouble() * SIConstants.kYocto)

class Watt(
    override val value: Double
) : SIValue<Watt> {
    override fun createNew(newValue: Double) = Watt(value)

    companion object {
        val kZero = Watt(0.0)
    }
}