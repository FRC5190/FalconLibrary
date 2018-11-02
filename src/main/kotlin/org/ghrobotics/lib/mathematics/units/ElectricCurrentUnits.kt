package org.ghrobotics.lib.mathematics.units

val Number.amp get() = ElectricCurrent(toDouble())

val Number.yottaamp get() = (toDouble() / 1e-24).amp
val Number.zettaamp get() = (toDouble() / 1e-21).amp
val Number.exaamp get() = (toDouble() / 1e-18).amp
val Number.petaamp get() = (toDouble() / 1e-15).amp
val Number.teraamp get() = (toDouble() / 1e-12).amp
val Number.gigaamp get() = (toDouble() / 1e-9).amp
val Number.megaamp get() = (toDouble() / 1e-6).amp
val Number.kiloamp get() = (toDouble() / 0.001).amp
val Number.hectoamp get() = (toDouble() / 0.01).amp
val Number.decaamp get() = (toDouble() / 0.1).amp
val Number.deciamp get() = (toDouble() / 10).amp
val Number.centiamp get() = (toDouble() / 100).amp
val Number.milliamp get() = (toDouble() / 1000).amp
val Number.microamp get() = (toDouble() / 1000000).amp
val Number.nanoamp get() = (toDouble() / 1e+9).amp
val Number.picoamp get() = (toDouble() / 1e+12).amp
val Number.femtoamp get() = (toDouble() / 1e+15).amp
val Number.attoamp get() = (toDouble() / 1e+18).amp
val Number.zeptoamp get() = (toDouble() / 1e+21).amp
val Number.yoctoamp get() = (toDouble() / 1e+24).amp

class ElectricCurrent(
    override val value: Double
) : SIUnit<ElectricCurrent> {
    val amp get() = value

    val yottaamp get() = value * 1e-24
    val zettaamp get() = value * 1e-21
    val exaamp get() = value * 1e-18
    val petaamp get() = value * 1e-15
    val teraamp get() = value * 1e-12
    val gigaamp get() = value * 1e-9
    val megaamp get() = value * 1e-6
    val kiloamp get() = value * 0.001
    val hectoamp get() = value * 0.01
    val decaamp get() = value * 0.1
    val deciamp get() = value * 10
    val centiamp get() = value * 100
    val milliamp get() = value * 1000
    val microamp get() = value * 1000000
    val nanoamp get() = value * 1e+9
    val picoamp get() = value * 1e+12
    val femtoamp get() = value * 1e+15
    val attoamp get() = value * 1e+18
    val zeptoamp get() = value * 1e+21
    val yoctoamp get() = value * 1e+24

    override fun createNew(newBaseValue: Double) = ElectricCurrent(newBaseValue)
}