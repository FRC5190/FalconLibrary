package org.ghrobotics.lib.mathematics


import org.ghrobotics.lib.mathematics.units.amp
import org.ghrobotics.lib.mathematics.units.derivedunits.feetPerMinute
import org.ghrobotics.lib.mathematics.units.derivedunits.feetPerSecond
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class DerivedTests {

    @Test
    fun testVelocity() {
        val one = 5.meter
        val two = 2.second

        val three = one / two

        assert(three.value == 2.5)
    }

    @Test
    fun testVelocityAdjust() {
        val one = 5.meter
        val two = 2.second

        val three = one / two

        val four = three.feetPerMinute

        assert(four epsilonEquals 492.12598425196853)
    }

    @Test
    fun testAcceleration() {
        val one = 10.meter / 2.second / 4.second

        assert(one.value == 1.25)
    }

    @Test
    fun testAccelerationToVelocity() {
        val one = 10.meter / 1.6.second / 2.second
        val two = 5.second

        val three = one * two

        val four = three.feetPerSecond

        assert(four epsilonEquals 51.26312335958006)
    }

    @Test
    fun testVelocityToLength() {
        val one = 5.meter / 2.second
        val two = 6.second

        val three = one * two
        val four = three.meter

        assert(four == 15.0)
    }

    @Test
    fun testVelocityAndAccelerationToTime() {
        val one = 22.meter / 2.second
        val two = 18.meter / 0.5.second / 4.second

        val three = one / two

        assert(three.second epsilonEquals 1.2222222222222223)
    }

    @Test
    fun testAccelerationDividedByAcceleration() {
        val one = 33.meter / 1.second / 1.second
        val two = 22.meter / 2.second / 1.second

        val three = one / two

        assert(three == 3.0)
    }

    @Test
    fun testVelocityDividedByVelocity() {
        val one = 33.meter / 1.second
        val two = 22.meter / 2.second

        val three = one / two

        assert(three epsilonEquals 3.0)
    }

    @Test
    fun testVoltage() {
        val one = 1.volt
        val two = 5.amp

        val three = one * two

        assert(three.value epsilonEquals 5.0)
    }

    @Test
    fun testOhm() {
        val one = 2.volt
        val two = 5.amp

        val three = one / two

        assert(three.value epsilonEquals 0.4)
    }
}