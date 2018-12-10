package org.ghrobotics.lib.subsystems.drive.localization

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.meter
import org.junit.Assert
import org.junit.Test

class InterpolatableLocalizationBufferTest {
    @Test
    fun testInterpolation() {
        val buffer = InterpolatableLocalizationBuffer()
        buffer[1000.0] = Pose2d()
        buffer[2000.0] = Pose2d(10.meter, 0.meter)

        Assert.assertEquals(Pose2d(5.meter, 0.meter), buffer.getInterpolated(1500.0))
    }
}