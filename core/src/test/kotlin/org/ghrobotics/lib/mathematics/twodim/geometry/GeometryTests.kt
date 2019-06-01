package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units.meter
import org.junit.Test

class GeometryTests {
    @Test
    fun testRectangleContains() {
        val rectangle = Rectangle2d(Translation2d(0.meter, 0.meter), Translation2d(10.meter, 10.meter))
        val translation = Translation2d(5.meter, 7.meter)
        assert(rectangle.contains(translation))
    }
}