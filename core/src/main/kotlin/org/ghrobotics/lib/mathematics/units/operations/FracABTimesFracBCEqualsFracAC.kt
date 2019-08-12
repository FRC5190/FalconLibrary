package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit

// (NativeUnit / Meter) * (Meter / Second) = NativeUnit / Second
// (A / B) * (B / C) = A / C
// (NativeUnit / 1) * (1 / Second)
// NativeUnit / Second

operator fun <A : SIKey, B : SIKey, C : SIKey> SIUnit<Frac<A, B>>.times(other: SIUnit<Frac<B, C>>) = SIUnit<Frac<A, C>>(value.times(other.value))