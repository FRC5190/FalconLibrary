package org.ghrobotics.lib.subsystems.drive.localization

import org.ghrobotics.lib.localization.TimeInterpolatableBuffer
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Assert
import org.junit.Test

class TimeInterpolatableBufferTest {
    @Test
    fun testInterpolation() {
        val buffer = TimeInterpolatableBuffer<Pose2d>(
            2.second,
            timeSource = { 2.second }
        )
        buffer[1000.second] = Pose2d()
        buffer[2000.second] = Pose2d(10.meter, 0.meter)

        Assert.assertEquals(Pose2d(), buffer[500.second])
        Assert.assertEquals(Pose2d(2.5.meter, 0.meter), buffer[1250.second])
        Assert.assertEquals(Pose2d(5.meter, 0.meter), buffer[1500.second])
        Assert.assertEquals(Pose2d(10.meter, 0.meter), buffer[2500.second])
    }
}