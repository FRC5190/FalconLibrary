package org.ghrobotics.lib.mathematics.units.derived

import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.Frac
import org.ghrobotics.lib.mathematics.units.Unitless

typealias Inverse<K> = Frac<Unitless, K>

typealias Curvature = Inverse<Meter>