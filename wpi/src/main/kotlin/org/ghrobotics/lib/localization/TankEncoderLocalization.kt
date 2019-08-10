/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.localization

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Twist2d
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.utils.Source

class TankEncoderLocalization(
    robotHeading: Source<Rotation2d>,
    val leftEncoder: Source<SIUnit<Meter>>,
    val rightEncoder: Source<SIUnit<Meter>>,
    localizationBuffer: TimePoseInterpolatableBuffer = TimePoseInterpolatableBuffer()
) : Localization(robotHeading, localizationBuffer) {

    private var prevLeftEncoder = 0.0.meter
    private var prevRightEncoder = 0.0.meter

    override fun resetInternal(newPosition: Pose2d) {
        super.resetInternal(newPosition)
        prevLeftEncoder = leftEncoder()
        prevRightEncoder = rightEncoder()
    }

    override fun update(deltaHeading: Rotation2d): Twist2d {
        val newLeftEncoder = leftEncoder()
        val newRightEncoder = rightEncoder()

        val deltaLeft = newLeftEncoder - prevLeftEncoder
        val deltaRight = newRightEncoder - prevRightEncoder

        this.prevLeftEncoder = newLeftEncoder
        this.prevRightEncoder = newRightEncoder

        return forwardKinematics(deltaLeft, deltaRight, deltaHeading)
    }

    /**
     * Return a twist that represents the robot's motion from the left delta, the right delta, and the rotation delta.
     */
    private fun forwardKinematics(
        leftDelta: SIUnit<Meter>,
        rightDelta: SIUnit<Meter>,
        rotationDelta: Rotation2d
    ): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx.value, 0.0, rotationDelta.radians)
    }
}