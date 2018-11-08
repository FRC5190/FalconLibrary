package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.millisecond

typealias NativeUnitVelocity = Velocity<NativeUnit>

val Number.STUPer100ms get() = STU / 100.millisecond

operator fun NativeUnit.div(other: Time) = NativeUnitVelocity(value / other.value, this)

val NativeUnitVelocity.STUPer100ms get() = value / 10

fun <T : SIUnit<T>> Velocity<T>.fromModel(model: NativeUnitModel<T>): NativeUnitVelocity =
    model.fromModel(type.createNew(value)).velocity

fun <T : SIUnit<T>> NativeUnitVelocity.toModel(model: NativeUnitModel<T>) =
    model.toModel(type.createNew(value)).velocity