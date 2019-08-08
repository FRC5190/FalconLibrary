package org.ghrobotics.lib.mathematics.units.derived

import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.Kilogram
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.Mult
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second

typealias Volt = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Ampere, Mult<Second, Mult<Second, Second>>>>

val Double.volt get() = SIUnit<Volt>(this)

val Number.volt get() = toDouble().volt