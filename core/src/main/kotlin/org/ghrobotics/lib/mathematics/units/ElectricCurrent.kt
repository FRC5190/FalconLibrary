package org.ghrobotics.lib.mathematics.units

val Number.yottaamp get() = ElectricCurrent(toDouble() * SIConstants.kYotta)
val Number.zettaamp get() = ElectricCurrent(toDouble() * SIConstants.kZetta)
val Number.exaamp get() = ElectricCurrent(toDouble() * SIConstants.kExa)
val Number.petaamp get() = ElectricCurrent(toDouble() * SIConstants.kPeta)
val Number.teraamp get() = ElectricCurrent(toDouble() * SIConstants.kTera)
val Number.gigaamp get() = ElectricCurrent(toDouble() * SIConstants.kGiga)
val Number.megaamp get() = ElectricCurrent(toDouble() * SIConstants.kMega)
val Number.kiloamp get() = ElectricCurrent(toDouble() * SIConstants.kKilo)
val Number.hectoamp get() = ElectricCurrent(toDouble() * SIConstants.kHecto)
val Number.decaamp get() = ElectricCurrent(toDouble() * SIConstants.kDeca)
val Number.amp get() = ElectricCurrent(toDouble())
val Number.deciamp get() = ElectricCurrent(toDouble() * SIConstants.kDeci)
val Number.centiamp get() = ElectricCurrent(toDouble() * SIConstants.kCenti)
val Number.milliamp get() = ElectricCurrent(toDouble() * SIConstants.kMilli)
val Number.microamp get() = ElectricCurrent(toDouble() * SIConstants.kMicro)
val Number.nanoamp get() = ElectricCurrent(toDouble() * SIConstants.kNano)
val Number.picoamp get() = ElectricCurrent(toDouble() * SIConstants.kPico)
val Number.femtoamp get() = ElectricCurrent(toDouble() * SIConstants.kFemto)
val Number.attoamp get() = ElectricCurrent(toDouble() * SIConstants.kAtto)
val Number.zeptoamp get() = ElectricCurrent(toDouble() * SIConstants.kZepto)
val Number.yoctoamp get() = ElectricCurrent(toDouble() * SIConstants.kYocto)

class ElectricCurrent(
    override val value: Double
) : SIUnit<ElectricCurrent> {
    val yottaamp get() = value / SIConstants.kYotta
    val zettaamp get() = value / SIConstants.kZetta
    val exaamp get() = value / SIConstants.kExa
    val petaamp get() = value / SIConstants.kPeta
    val teraamp get() = value / SIConstants.kTera
    val gigaamp get() = value / SIConstants.kGiga
    val megaamp get() = value / SIConstants.kMega
    val kiloamp get() = value / SIConstants.kKilo
    val hectoamp get() = value / SIConstants.kHecto
    val decaamp get() = value / SIConstants.kDeca
    val amp get() = value
    val deciamp get() = value / SIConstants.kDeci
    val centiamp get() = value / SIConstants.kCenti
    val milliamp get() = value / SIConstants.kMilli
    val microamp get() = value / SIConstants.kMicro
    val nanoamp get() = value / SIConstants.kNano
    val picoamp get() = value / SIConstants.kPico
    val femtoamp get() = value / SIConstants.kFemto
    val attoamp get() = value / SIConstants.kAtto
    val zeptoamp get() = value / SIConstants.kZepto
    val yoctoamp get() = value / SIConstants.kYocto

    override fun createNew(newValue: Double) = ElectricCurrent(newValue)

    companion object {
        val kZero = ElectricCurrent(0.0)
    }
}