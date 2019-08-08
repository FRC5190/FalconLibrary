package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Unitless

operator fun <A : SIKey> SIUnit<Unitless>.times(other: SIUnit<A>) = SIUnit<A>(value.times(other.value))