package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.amp
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class ExpressionTests {

    @Test
    fun testTwoSame() {
        val one = 5.meter
        val two = 3.meter

        val three = one * two

        val four = three.divA(one)
        val five = three.divB(two)

        assert(four.meter.asDouble epsilonEquals 3.0)
        assert(five.meter.asDouble epsilonEquals 5.0)
    }

    @Test
    fun testTwoDifferent() {
        val one = 5.meter
        val two = 3.second

        val three = one * two

        val four = three / one
        val five = three / two

        assert(four.second.asDouble epsilonEquals 3.0)
        assert(five.meter.asDouble epsilonEquals 5.0)
    }

    @Test
    fun testThreeDifferent() {
        val one = 5.meter
        val two = 3.second
        val three = 2.amp

        val four = one * two * three

        assert(four.asDouble epsilonEquals 30.0)
    }

}