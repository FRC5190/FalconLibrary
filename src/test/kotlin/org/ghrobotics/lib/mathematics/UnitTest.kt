package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.derivedunits.per
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nativeunits.NativeUnitSettings
import org.ghrobotics.lib.mathematics.units.nativeunits.STU
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import kotlin.math.absoluteValue

class UnitTest {

    private val settings = NativeUnitSettings(
        1440,
        3.0
    )

    @Test
    fun testNativeUnits() {
        val nativeUnits = 360.STU(settings)

        assert((nativeUnits.inch.asDouble - 4.71).absoluteValue < 0.005)
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