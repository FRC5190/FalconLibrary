package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit

operator fun <A : SIKey, B : SIKey> SIUnit<A>.times(other: SIUnit<Frac<B, A>>) = SIUnit<B>(value.times(other.value))