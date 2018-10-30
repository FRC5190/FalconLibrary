package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac11
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second

typealias NativeUnitVelocity = SIFrac11<NativeUnit, Time>

val Number.STUPer100ms get() = STU / 100.millisecond

operator fun NativeUnit.div(other: Time): NativeUnitVelocity = SIFrac11(value / other.value, this, other)

val NativeUnitVelocity.STUPer100ms get() = value / 10

fun <T : SIUnit<T>> SIFrac11<T, Time>.fromModel(model: NativeUnitModel<T>): NativeUnitVelocity =
        model.fromModel(this * 1.second) / 1.second

fun <T : SIUnit<T>> NativeUnitVelocity.toModel(model: NativeUnitModel<T>) =
        model.toModel(this * 1.second) / 1.second