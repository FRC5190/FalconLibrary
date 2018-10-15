package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.fractions.adjustBottom

typealias NativeUnitVelocity = SIFrac11<NativeUnit, Time>

val Number.STUPer100ms
    get() = STU per 100.millisecond

infix fun NativeUnit.per(other: Time): NativeUnitVelocity = div(other)
operator fun NativeUnit.div(other: Time): NativeUnitVelocity = SIFrac11(this, other)

val NativeUnitVelocity.STUPer100ms: NativeUnitVelocity
    get() = adjustBottom(SIPrefix.DECA, TimeUnits.Second)

fun <T : SIValue<T>> SIFrac11<T, Time>.fromModel(model: NativeUnitModel<T>): NativeUnitVelocity =
    SIFrac11(model.fromModel(top), bottom)

fun <T : SIValue<T>> NativeUnitVelocity.toModel(model: NativeUnitModel<T>) = SIFrac11(top.toModel(model), bottom)