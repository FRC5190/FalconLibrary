package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit

// (NativeUnit / Second) / (NativeUnit / Meter)  = Meter / Second
// (A / B) / (A / C) = (C / B)

// (NativeUnit / Second) * (Meter / NativeUnit) = Meter / Second
// (1 / Second) * (Meter / 1)

operator fun <A : SIKey, B : SIKey, C : SIKey> SIUnit<Frac<A, B>>.div(other: SIUnit<Frac<A, C>>) = SIUnit<Frac<C, B>>(value.div(other.value))