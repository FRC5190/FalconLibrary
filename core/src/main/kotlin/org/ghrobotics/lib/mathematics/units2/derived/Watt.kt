package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.Kilogram
import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.Mult
import org.ghrobotics.lib.mathematics.units2.SIUnit
import org.ghrobotics.lib.mathematics.units2.Second

typealias Watt = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Second, Mult<Second, Second>>>

val Double.watt get() = SIUnit<Watt>(this)

val Number.watt get() = toDouble().watt