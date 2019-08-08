package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Ampere
import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.Kilogram
import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.Mult
import org.ghrobotics.lib.mathematics.units2.Second

typealias Ohm = Frac<Mult<Kilogram, Mult<Meter, Meter>>,
    Mult<Second, Mult<Second, Mult<Second, Mult<Ampere, Ampere>>>>>