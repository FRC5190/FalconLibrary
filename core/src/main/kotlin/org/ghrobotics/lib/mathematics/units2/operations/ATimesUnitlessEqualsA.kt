package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Unitless

operator fun <A : SIKey> SIUnit<A>.times(other: SIUnit<Unitless>) = SIUnit<A>(value.times(other.value))