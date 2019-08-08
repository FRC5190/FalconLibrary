package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit

operator fun <A : SIKey, B : SIKey> SIUnit<Frac<A, B>>.times(other: SIUnit<B>) = SIUnit<A>(value.times(other.value))