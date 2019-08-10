/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.localization

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import org.ghrobotics.lib.localization.TimePoseInterpolatableBuffer
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Assert
import org.junit.Test

class TimeInterpolatableBufferTest {
    @Test
    fun testInterpolation() {
        val buffer = TimePoseInterpolatableBuffer(
            2.second,
            timeSource = { 2.second }
        )
        buffer[1000.second] = Pose2d()
        buffer[2000.second] = Pose2d(10.meter, 0.meter, Rotation2d())

        Assert.assertEquals(Pose2d().translation.norm, buffer[500.second]!!.translation.norm, kEpsilon)
        Assert.assertEquals(
            Pose2d(2.5.meter, 0.meter, Rotation2d()).translation.norm,
            buffer[1250.second]!!.translation.norm, kEpsilon
        )
        Assert.assertEquals(
            Pose2d(5.meter, 0.meter, Rotation2d()).translation.norm,
            buffer[1500.second]!!.translation.norm, kEpsilon
        )
        Assert.assertEquals(
            Pose2d(10.meter, 0.meter, Rotation2d()).translation.norm,
            buffer[2500.second]!!.translation.norm, kEpsilon
        )
    }
}