package org.ghrobotics.lib.mathematics.units.operations

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Unitless

operator fun <A : SIKey> SIUnit<A>.div(other: SIUnit<Unitless>) = SIUnit<A>(value.div(other.value))