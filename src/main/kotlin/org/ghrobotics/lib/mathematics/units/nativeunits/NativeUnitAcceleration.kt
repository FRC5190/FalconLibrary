package org.ghrobotics.lib.mathematics.units.nativeunits

import org.ghrobotics.lib.mathematics.units.SIPrefix
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.TimeUnits
import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.fractions.SIFrac12
import org.ghrobotics.lib.mathematics.units.fractions.adjustBottom

typealias NativeUnitAcceleration = SIFrac12<NativeUnit, Time, Time>

fun Acceleration.STU(settings: NativeUnitSettings): NativeUnitAcceleration = top.STU(settings) per bottom.a per bottom.b

val NativeUnitAcceleration.STUPer100msPerSecond: NativeUnitAcceleration
    get() = adjustBottom(
        SIPrefix.DECA, TimeUnits.Second,
        SIPrefix.BASE, TimeUnits.Second
    )