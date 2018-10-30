package org.ghrobotics.lib.mathematics.units

val Number.kilogram get() = Mass(toDouble())

val Number.yottagram get() = (toDouble() / 1e-21).kilogram
val Number.zettagram get() = (toDouble() / 1e-18).kilogram
val Number.exagram get() = (toDouble() / 1e-15).kilogram
val Number.petagram get() = (toDouble() / 1e-12).kilogram
val Number.teragram get() = (toDouble() / 1e-9).kilogram
val Number.gigagram get() = (toDouble() / 1e-6).kilogram
val Number.megagram get() = (toDouble() / 1e-3).kilogram
val Number.gram get() = (toDouble() / 1000).kilogram
val Number.hectogram get() = (toDouble() / 10).kilogram
val Number.decagram get() = (toDouble() / 100).kilogram
val Number.decigram get() = (toDouble() / 10000).kilogram
val Number.centigram get() = (toDouble() / 100000).kilogram
val Number.milligram get() = (toDouble() / 1000000).kilogram
val Number.microgram get() = (toDouble() / 1e+9).kilogram
val Number.nanogram get() = (toDouble() / 1e+12).kilogram
val Number.picogram get() = (toDouble() / 1e+15).kilogram
val Number.femtogram get() = (toDouble() / 1e+18).kilogram
val Number.attogram get() = (toDouble() / 1e+21).kilogram
val Number.zeptogram get() = (toDouble() / 1e+24).kilogram
val Number.yoctogram get() = (toDouble() / 1e+27).kilogram

class Mass(
        override val value: Double
) : SIUnit<Mass> {
    val kilogram get() = value

    val yottagram get() = value * 1e-21
    val zettagram get() = value * 1e-18
    val exagram get() = value * 1e-15
    val petagram get() = value * 1e-12
    val teragram get() = value * 1e-9
    val gigagram get() = value * 1e-6
    val megagram get() = value * 1e-3
    val gram get() = value / 1000
    val hectogram get() = value * 10
    val decagram get() = value * 100
    val decigram get() = value * 10000
    val centigram get() = value * 100000
    val milligram get() = value * 1000000
    val microgram get() = value * 1e+9
    val nanogram get() = value * 1e+12
    val picogram get() = value * 1e+15
    val femtogram get() = value * 1e+18
    val attogram get() = value * 1e+21
    val zeptogram get() = value * 1e+24
    val yoctogram get() = value * 1e+27

    override fun createNew(newBaseValue: Double) = Mass(newBaseValue)
}