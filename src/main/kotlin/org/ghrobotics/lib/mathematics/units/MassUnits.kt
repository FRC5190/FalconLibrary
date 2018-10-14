package org.ghrobotics.lib.mathematics.units

val Number.gram by length(MassUnits.Gram)

private fun length(type: MassUnits) = AbstractMass.createDelegate(type)

val Number.yottagram by gram(SIPrefix.YOTTA)
val Number.zettagram by gram(SIPrefix.ZETTA)
val Number.exagram by gram(SIPrefix.EXA)
val Number.petagram by gram(SIPrefix.PETA)
val Number.teragram by gram(SIPrefix.TERA)
val Number.gigagram by gram(SIPrefix.GIGA)
val Number.megagram by gram(SIPrefix.MEGA)
val Number.kilogram by gram(SIPrefix.KILO)
val Number.hectogram by gram(SIPrefix.HECTO)
val Number.decagram by gram(SIPrefix.DECA)
val Number.decigram by gram(SIPrefix.DECI)
val Number.centigram by gram(SIPrefix.CENTI)
val Number.milligram by gram(SIPrefix.MILLI)
val Number.microgram by gram(SIPrefix.MICRO)
val Number.nanogram by gram(SIPrefix.NANO)
val Number.picogram by gram(SIPrefix.PICO)
val Number.femtogram by gram(SIPrefix.FEMTO)
val Number.attogram by gram(SIPrefix.ATTO)
val Number.zeptogram by gram(SIPrefix.ZEPTO)
val Number.yoctogram by gram(SIPrefix.YOCTO)

private fun gram(prefix: SIPrefix) = AbstractMass.createMetricDelegate(prefix)

enum class MassUnits {
    Gram
}

interface Mass :
    SIUnit<Mass, MassUnits> {
    val gram: Mass

    val yottagram: Mass
    val zettagram: Mass
    val exagram: Mass
    val petagram: Mass
    val teragram: Mass
    val gigagram: Mass
    val megagram: Mass
    val kilogram: Mass
    val hectogram: Mass
    val decagram: Mass
    val decigram: Mass
    val centigram: Mass
    val milligram: Mass
    val microgram: Mass
    val nanogram: Mass
    val picogram: Mass
    val femtogram: Mass
    val attogram: Mass
    val zeptogram: Mass
    val yoctogram: Mass
}

class AbstractMass(
    value: Double,
    prefix: SIPrefix,
    type: MassUnits
) : AbstractSIUnit<Mass, MassUnits>(
    value,
    prefix,
    type,
    AbstractMass
), Mass {
    override val gram by convertUnit(MassUnits.Gram)

    override val yottagram by convertMetric(SIPrefix.YOTTA)
    override val zettagram by convertMetric(SIPrefix.ZETTA)
    override val exagram by convertMetric(SIPrefix.EXA)
    override val petagram by convertMetric(SIPrefix.PETA)
    override val teragram by convertMetric(SIPrefix.TERA)
    override val gigagram by convertMetric(SIPrefix.GIGA)
    override val megagram by convertMetric(SIPrefix.MEGA)
    override val kilogram by convertMetric(SIPrefix.KILO)
    override val hectogram by convertMetric(SIPrefix.HECTO)
    override val decagram by convertMetric(SIPrefix.DECA)
    override val decigram by convertMetric(SIPrefix.DECI)
    override val centigram by convertMetric(SIPrefix.CENTI)
    override val milligram by convertMetric(SIPrefix.MILLI)
    override val microgram by convertMetric(SIPrefix.MICRO)
    override val nanogram by convertMetric(SIPrefix.NANO)
    override val picogram by convertMetric(SIPrefix.PICO)
    override val femtogram by convertMetric(SIPrefix.FEMTO)
    override val attogram by convertMetric(SIPrefix.ATTO)
    override val zeptogram by convertMetric(SIPrefix.ZEPTO)
    override val yoctogram by convertMetric(SIPrefix.YOCTO)

    companion object : SIUnitConverter<Mass, MassUnits>(
        MassUnits.Gram,
        UnitMapper.massMapper
    ) {
        override fun create(newValue: Double, newPrefix: SIPrefix, newType: MassUnits): Mass =
            AbstractMass(newValue, newPrefix, newType)
    }

}