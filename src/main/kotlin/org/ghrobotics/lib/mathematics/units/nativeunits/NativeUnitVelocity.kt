package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIPrefix
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.TimeUnits
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.adjustBottom
import org.ghrobotics.lib.mathematics.units.millisecond

typealias NativeUnitVelocity = SIFrac11<NativeUnit, Time>

val Number.STUPer100ms
    get() = STU per 100.millisecond

fun Number.STUPer100ms(settings: NativeUnitSettings) = STUPer100ms.toVelocity(settings)

infix fun NativeUnit.per(other: Time): NativeUnitVelocity = div(other)
operator fun NativeUnit.div(other: Time): NativeUnitVelocity = SIFrac11(this, other)

fun Velocity.STU(settings: NativeUnitSettings): NativeUnitVelocity = top.STU(settings) per bottom

val NativeUnitVelocity.STUPer100ms
    get() = adjustBottom(SIPrefix.DECA, TimeUnits.Second)

fun NativeUnitVelocity.toVelocity(settings: NativeUnitSettings) = top.length(settings) per bottom