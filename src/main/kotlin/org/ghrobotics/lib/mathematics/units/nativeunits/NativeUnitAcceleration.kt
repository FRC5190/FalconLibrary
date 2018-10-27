package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.fractions.adjustBottom

typealias NativeUnitAcceleration = SIFrac12<NativeUnit, Time, Time>

fun <T : SIValue<T>> SIFrac12<T, Time, Time>.fromModel(model: NativeUnitModel<T>): NativeUnitAcceleration =
    SIFrac12(model.fromModel(top), bottom)

fun <T : SIValue<T>> NativeUnitAcceleration.toModel(model: NativeUnitModel<T>): SIFrac12<T, Time, Time> =
    SIFrac12(model.toModel(top), bottom)

val Number.STUPer100msPerSecond: NativeUnitAcceleration
    get() = STUPer100ms per 1.second

val NativeUnitAcceleration.STUPer100msPerSecond: NativeUnitAcceleration
    get() = adjustBottom(
        SIPrefix.DECI, TimeUnits.Second,
        SIPrefix.BASE, TimeUnits.Second
    )