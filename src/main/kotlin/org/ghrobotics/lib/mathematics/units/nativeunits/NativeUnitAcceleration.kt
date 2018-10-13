package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.per

infix fun NativeUnitVelocity.per(other: Time): NativeUnitAcceleration = div(other)
operator fun NativeUnitVelocity.div(other: Time): NativeUnitAcceleration = NativeUnitAccelerationImpl(this, other)

fun Acceleration.STU(settings: NativeUnitSettings): NativeUnitAcceleration = top.STU(settings) per bottom

interface NativeUnitAcceleration : SIChainedFraction<NativeUnitVelocity, Time, TimeUnits, NativeUnitAcceleration> {
    val STUPer100msPerSecond: NativeUnitAcceleration

    fun toAcceleration(settings: NativeUnitSettings) = top.toVelocity(settings) per bottom
}

class NativeUnitAccelerationImpl(
    top: NativeUnitVelocity,
    bottom: Time
) : NativeUnitAcceleration, AbstractSIChainedFraction<NativeUnitVelocity, Time, TimeUnits, NativeUnitAcceleration>(
    top,
    bottom
) {
    override val STUPer100msPerSecond: NativeUnitAcceleration
        get() = create(
            top.STUPer100ms,
            bottom.convertTo(SIPrefix.BASE, TimeUnits.Second)
        )

    override fun create(newTop: NativeUnitVelocity, newBottom: Time) =
        NativeUnitAccelerationImpl(newTop, newBottom)

}