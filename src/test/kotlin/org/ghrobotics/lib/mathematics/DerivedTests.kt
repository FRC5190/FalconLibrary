package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.LengthUnits
import org.ghrobotics.lib.mathematics.units.TimeUnits
import org.ghrobotics.lib.mathematics.units.derivedunits.per
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class DerivedTests {

    @Test
    fun testVelocity() {
        val one = 5.meter
        val two = 2.second

        val three = one per two

        assert(three.asDouble == 2.5)
    }

    @Test
    fun testVelocityAdjust() {
        val one = 5.meter
        val two = 2.second

        val three = one per two

        val four = three.adjust(
            LengthUnits.Feet,
            TimeUnits.Minute
        )

        print(four.asDouble)
        assert(four.asDouble epsilonEquals  492.12598425196853)
    }

    @Test
    fun testAcceleration() {
        val one = 10.meter per 2.second per 4.second

        assert(one.asDouble == 1.25)
    }

    @Test
    fun testAccelerationToVelocity() {
        val one = 10.meter per 1.6.second per 2.second
        val two = 5.second

        val three = one * two

        val four = three.adjust(
            LengthUnits.Meter,
            TimeUnits.Second
        )

        assert(four.asDouble epsilonEquals 15.625)
    }

    @Test
    fun testVelocityToLength() {
        val one = 5.meter per 2.second
        val two = 6.second

        val three = one * two
        val four = three.meter

        assert(four.asDouble == 15.0)
    }

    @Test
    fun testVelocityAndAccelerationToTime() {
        val one = 22.meter per 2.second
        val two = 18.meter per 0.5.second per 4.second

        val three = one / two

        assert(three.second.asDouble epsilonEquals 1.2222222222222223)
    }

    @Test
    fun testAccelerationDividedByAcceleration() {
        val one = 33.meter per 1.second per 1.second
        val two = 22.meter per 2.second per 1.second

        val three = one / two

        assert(three == 3.0)
    }

    @Test
    fun testVelocityDividedByVelocity() {
        val one = 33.meter per 1.second
        val two = 22.meter per 2.second

        val three = one / two

        assert(three == 3.0)
    }

}