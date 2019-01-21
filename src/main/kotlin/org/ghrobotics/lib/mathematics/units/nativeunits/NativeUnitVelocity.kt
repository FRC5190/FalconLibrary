package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.millisecond

typealias NativeUnitVelocity = Velocity<NativeUnit>

val Number.STUPer100ms: NativeUnitVelocity get() = STU / 100.millisecond

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
operator fun NativeUnit.div(other: Time) = NativeUnitVelocity(value / other.value, this)

val NativeUnitVelocity.STUPer100ms get() = value / 10

fun <T : SIUnit<T>> Velocity<T>.toNativeUnitVelocity(model: NativeUnitModel<T>): NativeUnitVelocity =
    model.toNativeUnitVelocity(this)

fun <T : SIUnit<T>> NativeUnitVelocity.fromNativeUnitVelocity(model: NativeUnitModel<T>): Velocity<T> =
    model.fromNativeUnitVelocity(this)