package org.ghrobotics.lib.mathematics.units

val Number.meter get() = Length(toDouble())
val Number.thou get() = (toDouble() / 1000).inch
val Number.line get() = (toDouble() / 12.0).inch
val Number.inch get() = (toDouble() * 0.0254).meter
val Number.feet get() = (toDouble() * 12).inch
val Number.yard get() = (toDouble() * 3).feet
val Number.mile get() = (toDouble() * 5280).feet
val Number.league get() = (toDouble() * 3.0).mile
val Number.lightYear get() = (toDouble() * 9460730472580800.0).meter

val Number.yottameter get() = (toDouble() / 1e-24).meter
val Number.zettameter get() = (toDouble() / 1e-21).meter
val Number.exameter get() = (toDouble() / 1e-18).meter
val Number.petameter get() = (toDouble() / 1e-15).meter
val Number.terameter get() = (toDouble() / 1e-12).meter
val Number.gigameter get() = (toDouble() / 1e-9).meter
val Number.megameter get() = (toDouble() / 1e-6).meter
val Number.kilometer get() = (toDouble() / 0.001).meter
val Number.hectometer get() = (toDouble() / 0.01).meter
val Number.decameter get() = (toDouble() / 0.1).meter
val Number.decimeter get() = (toDouble() / 10).meter
val Number.centimeter get() = (toDouble() / 100).meter
val Number.millimeter get() = (toDouble() / 1000).meter
val Number.micrometer get() = (toDouble() / 1000000).meter
val Number.nanometer get() = (toDouble() / 1e+9).meter
val Number.picometer get() = (toDouble() / 1e+12).meter
val Number.femtometer get() = (toDouble() / 1e+15).meter
val Number.attometer get() = (toDouble() / 1e+18).meter
val Number.zeptometer get() = (toDouble() / 1e+21).meter
val Number.yoctometer get() = (toDouble() / 1e+24).meter

class Length(
        override val value: Double
) : SIUnit<Length> {
    val meter get() = value
    val thou get() = inch * 1000
    val line get() = inch * 12
    val inch get() = value / 0.0254
    val feet get() = inch / 12
    val yard get() = feet / 3
    val mile get() = feet / 5280
    val league get() = mile / 3
    val nauticalMile get() = meter / 1852
    val lightYear get() = meter / 9460730472580800

    val yottameter get() = value * 1e-24
    val zettameter get() = value * 1e-21
    val exameter get() = value * 1e-18
    val petameter get() = value * 1e-15
    val terameter get() = value * 1e-12
    val gigameter get() = value * 1e-9
    val megameter get() = value * 1e-6
    val kilometer get() = value * 0.001
    val hectometer get() = value * 0.01
    val decameter get() = value * 0.1
    val decimeter get() = value * 10
    val centimeter get() = value * 100
    val millimeter get() = value * 1000
    val micrometer get() = value * 1000000
    val nanometer get() = value * 1e+9
    val picometer get() = value * 1e+12
    val femtometer get() = value * 1e+15
    val attometer get() = value * 1e+18
    val zeptometer get() = value * 1e+21
    val yoctometer get() = value * 1e+24

    override fun createNew(newBaseValue: Double) = Length(newBaseValue)
}