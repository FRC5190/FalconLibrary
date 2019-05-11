package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity

typealias NativeUnitVelocity = Velocity<NativeUnit>

@Deprecated("Use nativeUnits naming instead of STU", ReplaceWith("nativeUnitsPer100ms"))
val Number.STUPer100ms: NativeUnitVelocity
    get() = nativeUnitsPer100ms
val Number.nativeUnitsPer100ms: NativeUnitVelocity get() = NativeUnitVelocity(toDouble() * 10, NativeUnit.kZero)

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun NativeUnit.div(other: Time) = NativeUnitVelocity(value / other.value, this)

@Deprecated("Use nativeUnits naming instead of STU", ReplaceWith("nativeUnitsPer100ms"))
val NativeUnitVelocity.STUPer100ms
    get() = nativeUnitsPer100ms
val NativeUnitVelocity.nativeUnitsPer100ms get() = value / 10.0

fun <T : SIUnit<T>> Velocity<T>.toNativeUnitVelocity(model: NativeUnitModel<T>): NativeUnitVelocity =
    model.toNativeUnitVelocity(this)

fun <T : SIUnit<T>> NativeUnitVelocity.fromNativeUnitVelocity(model: NativeUnitModel<T>): Velocity<T> =
    model.fromNativeUnitVelocity(this)