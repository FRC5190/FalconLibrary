package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Ampere
import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.Kilogram
import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.Mult
import org.ghrobotics.lib.mathematics.units2.Second

typealias Volt = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Ampere, Mult<Second, Mult<Second, Second>>>>