package org.ghrobotics.lib.mathematics.units2.derived

import org.ghrobotics.lib.mathematics.units2.Meter
import org.ghrobotics.lib.mathematics.units2.Frac
import org.ghrobotics.lib.mathematics.units2.Unitless

typealias Inverse<K> = Frac<Unitless, K>

typealias Curvature = Inverse<Meter>