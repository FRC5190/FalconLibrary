package org.ghrobotics.lib.mathematics.units

val Number.yottagram get() = Mass(toDouble() * SIConstants.kYottaOffsetKilo)
val Number.zettagram get() = Mass(toDouble() * SIConstants.kZettaOffsetKilo)
val Number.exagram get() = Mass(toDouble() * SIConstants.kExaOffsetKilo)
val Number.petagram get() = Mass(toDouble() * SIConstants.kPetaOffsetKilo)
val Number.teragram get() = Mass(toDouble() * SIConstants.kTeraOffsetKilo)
val Number.gigagram get() = Mass(toDouble() * SIConstants.kGigaOffsetKilo)
val Number.megagram get() = Mass(toDouble() * SIConstants.kMegaOffsetKilo)
val Number.kilogram get() = Mass(toDouble())
val Number.hectogram get() = Mass(toDouble() * SIConstants.kHectoOffsetKilo)
val Number.decagram get() = Mass(toDouble() * SIConstants.kDecaOffsetKilo)
val Number.gram get() = Mass(toDouble() * SIConstants.kBaseOffsetKilo)
val Number.decigram get() = Mass(toDouble() * SIConstants.kDeciOffsetKilo)
val Number.centigram get() = Mass(toDouble() * SIConstants.kCentiOffsetKilo)
val Number.milligram get() = Mass(toDouble() * SIConstants.kMilliOffsetKilo)
val Number.microgram get() = Mass(toDouble() * SIConstants.kMicroOffsetKilo)
val Number.nanogram get() = Mass(toDouble() * SIConstants.kNanoOffsetKilo)
val Number.picogram get() = Mass(toDouble() * SIConstants.kPicoOffsetKilo)
val Number.femtogram get() = Mass(toDouble() * SIConstants.kFemtoOffsetKilo)
val Number.attogram get() = Mass(toDouble() * SIConstants.kAttoOffsetKilo)
val Number.zeptogram get() = Mass(toDouble() * SIConstants.kZeptoOffsetKilo)
val Number.yoctogram get() = Mass(toDouble() * SIConstants.kYoctoOffsetKilo)

val Number.lb get() = Mass(toDouble() * SIMassConstants.kLbOffsetKilo)


class Mass(
    override val value: Double
) : SIUnit<Mass> {
    val yottagram get() = value / SIConstants.kYottaOffsetKilo
    val zettagram get() = value / SIConstants.kZettaOffsetKilo
    val exagram get() = value / SIConstants.kExaOffsetKilo
    val petagram get() = value / SIConstants.kPetaOffsetKilo
    val teragram get() = value / SIConstants.kTeraOffsetKilo
    val gigagram get() = value / SIConstants.kGigaOffsetKilo
    val megagram get() = value / SIConstants.kMegaOffsetKilo
    val kilogram get() = value
    val hectogram get() = value / SIConstants.kHectoOffsetKilo
    val decagram get() = value / SIConstants.kDecaOffsetKilo
    val gram get() = value / SIConstants.kBaseOffsetKilo
    val decigram get() = value / SIConstants.kDeciOffsetKilo
    val centigram get() = value / SIConstants.kCentiOffsetKilo
    val milligram get() = value / SIConstants.kMilliOffsetKilo
    val microgram get() = value / SIConstants.kMicroOffsetKilo
    val nanogram get() = value / SIConstants.kNanoOffsetKilo
    val picogram get() = value / SIConstants.kPicoOffsetKilo
    val femtogram get() = value / SIConstants.kFemtoOffsetKilo
    val attogram get() = value / SIConstants.kAttoOffsetKilo
    val zeptogram get() = value / SIConstants.kZeptoOffsetKilo
    val yoctogram get() = value / SIConstants.kYoctoOffsetKilo

    val lb get() = value / SIMassConstants.kLbOffsetKilo

    override fun createNew(newValue: Double) = Mass(newValue)

    companion object {
        val kZero = Mass(0.0)
    }
}

internal object SIMassConstants {
    const val kLbOffsetKilo = 0.453592
}