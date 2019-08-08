package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.units2.meter
import org.junit.Test

class GeometryTests {
    @Test
    fun testRectangleContains() {
        val rectangle = Rectangle2d(Translation2d(0.0.meter, 0.0.meter), Translation2d(10.0.meter, 10.0.meter))
        val translation = Translation2d(5.0.meter, 7.0.meter)
        assert(rectangle.contains(translation))
    }
}