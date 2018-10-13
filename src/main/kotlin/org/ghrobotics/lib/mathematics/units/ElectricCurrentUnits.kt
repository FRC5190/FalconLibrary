package org.ghrobotics.lib.mathematics.units

val Number.amp by length(ElectricCurrentUnits.Amp)

private fun length(type: ElectricCurrentUnits) = AbstractElectricCurrent.createDelegate(type)

val Number.yottaamp by amp(SIPrefix.YOTTA)
val Number.zettaamp by amp(SIPrefix.ZETTA)
val Number.exaamp by amp(SIPrefix.EXA)
val Number.petaamp by amp(SIPrefix.PETA)
val Number.teraamp by amp(SIPrefix.TERA)
val Number.gigaamp by amp(SIPrefix.GIGA)
val Number.megaamp by amp(SIPrefix.MEGA)
val Number.kiloamp by amp(SIPrefix.KILO)
val Number.hectoamp by amp(SIPrefix.HECTO)
val Number.decaamp by amp(SIPrefix.DECA)
val Number.deciamp by amp(SIPrefix.DECI)
val Number.centiamp by amp(SIPrefix.CENTI)
val Number.milliamp by amp(SIPrefix.MILLI)
val Number.microamp by amp(SIPrefix.MICRO)
val Number.nanoamp by amp(SIPrefix.NANO)
val Number.picoamp by amp(SIPrefix.PICO)
val Number.femtoamp by amp(SIPrefix.FEMTO)
val Number.attoamp by amp(SIPrefix.ATTO)
val Number.zeptoamp by amp(SIPrefix.ZEPTO)
val Number.yoctoamp by amp(SIPrefix.YOCTO)

private fun amp(prefix: SIPrefix) = AbstractElectricCurrent.createMetricDelegate(prefix)

enum class ElectricCurrentUnits {
    Amp
}

interface ElectricCurrent :
    SIUnit<ElectricCurrent, ElectricCurrentUnits> {
    val amp: ElectricCurrent

    val yottaamp: ElectricCurrent
    val zettaamp: ElectricCurrent
    val exaamp: ElectricCurrent
    val petaamp: ElectricCurrent
    val teraamp: ElectricCurrent
    val gigaamp: ElectricCurrent
    val megaamp: ElectricCurrent
    val kiloamp: ElectricCurrent
    val hectoamp: ElectricCurrent
    val decaamp: ElectricCurrent
    val deciamp: ElectricCurrent
    val centiamp: ElectricCurrent
    val milliamp: ElectricCurrent
    val microamp: ElectricCurrent
    val nanoamp: ElectricCurrent
    val picoamp: ElectricCurrent
    val femtoamp: ElectricCurrent
    val attoamp: ElectricCurrent
    val zeptoamp: ElectricCurrent
    val yoctoamp: ElectricCurrent
}

class AbstractElectricCurrent(
    value: Double,
    prefix: SIPrefix,
    type: ElectricCurrentUnits
) : AbstractSIUnit<ElectricCurrent, ElectricCurrentUnits>(
    value,
    prefix,
    type,
    AbstractElectricCurrent
), ElectricCurrent {
    override val amp by convertUnit(ElectricCurrentUnits.Amp)

    override val yottaamp by convertMetric(SIPrefix.YOTTA)
    override val zettaamp by convertMetric(SIPrefix.ZETTA)
    override val exaamp by convertMetric(SIPrefix.EXA)
    override val petaamp by convertMetric(SIPrefix.PETA)
    override val teraamp by convertMetric(SIPrefix.TERA)
    override val gigaamp by convertMetric(SIPrefix.GIGA)
    override val megaamp by convertMetric(SIPrefix.MEGA)
    override val kiloamp by convertMetric(SIPrefix.KILO)
    override val hectoamp by convertMetric(SIPrefix.HECTO)
    override val decaamp by convertMetric(SIPrefix.DECA)
    override val deciamp by convertMetric(SIPrefix.DECI)
    override val centiamp by convertMetric(SIPrefix.CENTI)
    override val milliamp by convertMetric(SIPrefix.MILLI)
    override val microamp by convertMetric(SIPrefix.MICRO)
    override val nanoamp by convertMetric(SIPrefix.NANO)
    override val picoamp by convertMetric(SIPrefix.PICO)
    override val femtoamp by convertMetric(SIPrefix.FEMTO)
    override val attoamp by convertMetric(SIPrefix.ATTO)
    override val zeptoamp by convertMetric(SIPrefix.ZEPTO)
    override val yoctoamp by convertMetric(SIPrefix.YOCTO)

    companion object : SIUnitConverter<ElectricCurrent, ElectricCurrentUnits>(
        ElectricCurrentUnits.Amp,
        UnitMapper.electricCurrentMapper
    ) {
        override fun create(newValue: Double, newPrefix: SIPrefix, newType: ElectricCurrentUnits): ElectricCurrent =
            AbstractElectricCurrent(newValue, newPrefix, newType)
    }

}