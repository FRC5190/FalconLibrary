package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitSettings
import org.ghrobotics.lib.mathematics.units.nativeunits.STU
import org.ghrobotics.lib.mathematics.units.nativeunits.STUPer100ms
import org.ghrobotics.lib.mathematics.units.nativeunits.STUPer100msPerSecond
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class UnitTest {

    private val settings = NativeUnitSettings(
        1440,
        3.0.inch
    )

    @Test
    fun testNativeUnits() {
        val nativeUnits = 360.STU(settings)

        assert(nativeUnits.inch.asDouble epsilonEquals 4.71238898038469)
    }

    @Test
    fun testVelocitySTU() {
        val one = 1.meter per 1.second

        val two = one.STU(settings)

        assert(two.STUPer100ms.asDouble == 30080.0)
    }

    @Test
    fun testAccelerationSTU() {
        val one = 1.meter per 1.second per 1.second

        val two = one.STU(settings)

        assert(two.STUPer100msPerSecond.asDouble == 30080.0)
    }

}