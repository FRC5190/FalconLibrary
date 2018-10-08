package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.IN
import org.ghrobotics.lib.mathematics.units.M
import org.ghrobotics.lib.mathematics.units.NativeUnitSettings
import org.ghrobotics.lib.mathematics.units.STU
import org.junit.Test
import kotlin.math.absoluteValue

class UnitTest {

    @Test
    fun testAddition() {
        val one = 5.IN
        val two = 3.5.M

        assert(((two - one).IN.asDouble - 132.795).absoluteValue < 0.001)
    }

    @Test
    fun testNativeUnits() {
        val settings = NativeUnitSettings(
            1440,
            3.0
        )
        val nativeUnits = 360.STU(settings)

        assert((nativeUnits.IN.asDouble - 4.71).absoluteValue < 0.005)
    }

}