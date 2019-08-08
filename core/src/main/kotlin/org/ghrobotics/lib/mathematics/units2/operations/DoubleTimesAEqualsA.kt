package org.ghrobotics.lib.mathematics.units2.operations

import org.ghrobotics.lib.mathematics.units2.SIKey
import org.ghrobotics.lib.mathematics.units2.SIUnit

operator fun <A : SIKey> Double.times(other: SIUnit<A>) = other.times(this)