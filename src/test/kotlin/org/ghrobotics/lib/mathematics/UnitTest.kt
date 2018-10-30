package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nativeunits.*
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class UnitTest {

    private val settings = NativeUnitLengthModel(
            1440.STU,
            3.0.inch
    )

    @Test
    fun testNativeUnits() {
        val nativeUnits = 360.STU.toModel(settings)

        assert(nativeUnits.inch epsilonEquals 4.71238898038469)
    }

    @Test
    fun testVelocitySTU() {
        val one = 1.meter / 1.second

        val two = one.fromModel(settings)

        val three = two.STUPer100ms

        assert(three.toInt() == 300)
    }

    @Test
    fun testAccelerationSTU() {
        val one = 1.meter / 1.second / 1.second

        val two = one.fromModel(settings)

        assert(two.STUPer100msPerSecond.toInt() == 300)
    }

    @Test
    fun testFeetToMeter() {
        val one = 1.feet

        assert(one.meter epsilonEquals 0.3048)
    }

}