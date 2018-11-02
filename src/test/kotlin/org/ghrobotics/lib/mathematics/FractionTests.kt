package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.derivedunits.Acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class FractionTests {

    @Test
    fun testVelocityToLength() {
        val one = 4.meter.velocity
        val two = 6.second

        val three: Acceleration = one / two

        println(three)
    }
}