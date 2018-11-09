package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.second

typealias NativeUnitAcceleration = Acceleration<NativeUnit>

fun <T : SIUnit<T>> Acceleration<T>.fromModel(model: NativeUnitModel<T>): NativeUnitAcceleration =
    model.fromModel(type.createNew(value)).acceleration

fun <T : SIUnit<T>> NativeUnitAcceleration.toModel(model: NativeUnitModel<T>): Acceleration<T> =
    model.toModel(type.createNew(value)).acceleration

val Number.STUPer100msPerSecond: NativeUnitAcceleration
    get() = STUPer100ms / 1.second

val NativeUnitAcceleration.STUPer100msPerSecond get() = (this * 1.second).STUPer100ms