package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Unitless

operator fun <A : SIKey> SIUnit<A>.div(other: SIUnit<Unitless>) = SIUnit<A>(value.div(other.value))