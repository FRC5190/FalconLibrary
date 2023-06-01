/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.localization

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import org.ghrobotics.lib.localization.TimePoseInterpolatableBuffer
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.mathematics.units.seconds
import org.junit.Assert
import org.junit.Test

class TimeInterpolatableBufferTest {
    @Test
    fun testInterpolation() {
        val buffer = TimePoseInterpolatableBuffer(
            2.seconds,
            timeSource = { 2.seconds },
        )
        buffer[1000.seconds] = Pose2d()
        buffer[2000.seconds] = Pose2d(10.meters, 0.meters, Rotation2d())

        Assert.assertEquals(Pose2d().translation.norm, buffer[500.seconds]!!.translation.norm, kEpsilon)
        Assert.assertEquals(
            Pose2d(2.5.meters, 0.meters, Rotation2d()).translation.norm,
            buffer[1250.seconds]!!.translation.norm,
            kEpsilon,
        )
        Assert.assertEquals(
            Pose2d(5.meters, 0.meters, Rotation2d()).translation.norm,
            buffer[1500.seconds]!!.translation.norm,
            kEpsilon,
        )
        Assert.assertEquals(
            Pose2d(10.meters, 0.meters, Rotation2d()).translation.norm,
            buffer[2500.seconds]!!.translation.norm,
            kEpsilon,
        )
    }
}
