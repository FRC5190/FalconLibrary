package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration

typealias NativeUnitAcceleration = Acceleration<NativeUnit>

fun <T : SIUnit<T>> Acceleration<T>.toNativeUnitAcceleration(model: NativeUnitModel<T>): NativeUnitAcceleration =
    model.toNativeUnitAcceleration(this)

fun <T : SIUnit<T>> NativeUnitAcceleration.fromNativeUnitAcceleration(model: NativeUnitModel<T>): Acceleration<T> =
    model.fromNativeUnitAcceleration(this)

@Deprecated("Use nativeUnits naming instead of STU", ReplaceWith("nativeUnitsPer100msPerSecond"))
val Number.STUPer100msPerSecond
    get() = nativeUnitsPer100msPerSecond
val Number.nativeUnitsPer100msPerSecond: NativeUnitAcceleration
    get() = NativeUnitAcceleration(toDouble() * 10.0, NativeUnit.kZero)

@Deprecated("Use nativeUnits naming instead of STU", ReplaceWith("nativeUnitsPer100msPerSecond"))
val NativeUnitAcceleration.STUPer100msPerSecond
    get() = nativeUnitsPer100msPerSecond
val NativeUnitAcceleration.nativeUnitsPer100msPerSecond get() = value / 10.0