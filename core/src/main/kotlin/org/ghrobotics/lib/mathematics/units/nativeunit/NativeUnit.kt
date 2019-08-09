/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units.nativeunit

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Velocity

object NativeUnit : SIKey

val Double.nativeUnits get() = SIUnit<NativeUnit>(this)
val Double.STU get() = nativeUnits

val Number.nativeUnits get() = toDouble().nativeUnits
val Number.STU get() = toDouble().STU

fun <K : SIKey> SIUnit<K>.toNativeUnitPosition(model: NativeUnitModel<K>): SIUnit<NativeUnit> =
    model.toNativeUnitPosition(this)

fun <K : SIKey> SIUnit<NativeUnit>.fromNativeUnitPosition(model: NativeUnitModel<K>): SIUnit<K> =
    model.fromNativeUnitPosition(this)

typealias NativeUnitVelocity = Velocity<NativeUnit>

val Double.nativeUnitsPer100ms get() = SIUnit<NativeUnitVelocity>(times(10.0))
val Double.STUPer100ms get() = nativeUnitsPer100ms

val Number.nativeUnitsPer100ms get() = toDouble().nativeUnitsPer100ms
val Number.STUPer100ms get() = toDouble().STUPer100ms

val SIUnit<NativeUnitVelocity>.nativeUnitsPer100ms get() = value.div(10.0)
val SIUnit<NativeUnitVelocity>.STUPer100ms get() = nativeUnitsPer100ms

fun <K : SIKey> SIUnit<Velocity<K>>.toNativeUnitVelocity(model: NativeUnitModel<K>): SIUnit<NativeUnitVelocity> =
    model.toNativeUnitVelocity(this)

fun <K : SIKey> SIUnit<NativeUnitVelocity>.fromNativeUnitVelocity(model: NativeUnitModel<K>): SIUnit<Velocity<K>> =
    model.fromNativeUnitVelocity(this)

typealias NativeUnitAcceleration = Acceleration<NativeUnit>

val Double.nativeUnitsPer100msPerSecond get() = SIUnit<NativeUnitAcceleration>(times(10.0))
val Double.STUPer100msPerSecond get() = nativeUnitsPer100msPerSecond

val Number.nativeUnitsPer100msPerSecond get() = toDouble().nativeUnitsPer100msPerSecond
val Number.STUPer100msPerSecond get() = toDouble().STUPer100msPerSecond

val SIUnit<NativeUnitAcceleration>.nativeUnitsPer100msPerSecond get() = value.div(10.0)
val SIUnit<NativeUnitAcceleration>.STUPer100msPerSecond get() = nativeUnitsPer100msPerSecond

fun <K : SIKey> SIUnit<Acceleration<K>>.toNativeUnitAcceleration(model: NativeUnitModel<K>): SIUnit<NativeUnitAcceleration> =
    model.toNativeUnitAcceleration(this)

fun <K : SIKey> SIUnit<NativeUnitAcceleration>.fromNativeUnitAcceleration(model: NativeUnitModel<K>): SIUnit<Acceleration<K>> =
    model.fromNativeUnitAcceleration(this)