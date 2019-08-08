package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.Mult
import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit

operator fun <A : SIKey, B : SIKey> SIUnit<A>.times(other: SIUnit<B>) = SIUnit<Mult<A, B>>(value.times(other.value))