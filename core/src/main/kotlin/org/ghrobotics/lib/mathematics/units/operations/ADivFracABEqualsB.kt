package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit

operator fun <A : SIKey, B : SIKey> SIUnit<A>.div(other: SIUnit<Frac<A, B>>) = SIUnit<B>(value.div(other.value))