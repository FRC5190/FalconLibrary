package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.per

val Number.STUPer100ms
    get() = STU per 100.millisecond

fun Number.STUPer100ms(settings: NativeUnitSettings): Velocity =
    NativeUnitVelocityImpl(this.STU, 100.millisecond).toVelocity(settings)

infix fun NativeUnit.per(other: Time): NativeUnitVelocity = div(other)
operator fun NativeUnit.div(other: Time): NativeUnitVelocity = NativeUnitVelocityImpl(this, other)

fun Velocity.STU(settings: NativeUnitSettings): NativeUnitVelocity = top.STU(settings) per bottom

interface NativeUnitVelocity : SIBaseFraction<NativeUnit, Time, TimeUnits, NativeUnitVelocity> {
    val STUPer100ms: NativeUnitVelocity
        get() = adjustBottom(TimeUnits.Second, SIPrefix.DECA)

    fun toVelocity(settings: NativeUnitSettings) = top.length(settings) per bottom
}

class NativeUnitVelocityImpl(
    top: NativeUnit,
    bottom: Time
) : NativeUnitVelocity, AbstractBaseSIFraction<NativeUnit, Time, TimeUnits, NativeUnitVelocity>(
    top,
    bottom
) {
    override fun create(newTop: NativeUnit, newBottom: Time) =
        NativeUnitVelocityImpl(newTop, newBottom)

    override fun div(other: NativeUnitVelocity): Double {
        val aN = this.top
        val bN = this.bottom.asMetric

        val cN = other.bottom.asMetric
        val dN = other.top

        return (aN.asDouble * cN.asDouble) / (bN.asDouble * dN.asDouble)
    }

}