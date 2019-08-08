package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit

operator fun <A : SIKey, B : SIKey> SIUnit<A>.div(other: SIUnit<Frac<A, B>>) = SIUnit<B>(value.div(other.value))