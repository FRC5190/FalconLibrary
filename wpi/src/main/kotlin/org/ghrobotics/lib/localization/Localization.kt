/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.localization

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Twist2d
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.utils.Source
import kotlin.reflect.KProperty

/**
 * @param localizationBuffer Stores the previous 100 states so that we can interpolate if needed.
 * Especially useful for Vision
 */
@Deprecated("The FalconLibrary localization class is no longer supported.")
abstract class Localization(
    protected val robotHeading: Source<Rotation2d>,
    private val localizationBuffer: TimePoseInterpolatableBuffer = TimePoseInterpolatableBuffer(),
) : Source<Pose2d> {

    /**
     * The robot position relative to the field.
     */
    var robotPosition = Pose2d()
        private set

    /**
     * Stores the previous state of the robot.
     */
    private var prevHeading = Rotation2d(0.0)
    private var headingOffset = Rotation2d(0.0)

    @Synchronized
    fun reset(newPosition: Pose2d = Pose2d()) = resetInternal(newPosition)

    protected open fun resetInternal(newPosition: Pose2d) {
        robotPosition = newPosition
        val newHeading = robotHeading()
        prevHeading = newHeading
        headingOffset = -newHeading + newPosition.rotation
        localizationBuffer.clear()
    }

    @Synchronized
    fun update() {
        val newHeading = robotHeading()

        val deltaHeading = newHeading - prevHeading

        // Add the recorded motion of the robot during this iteration to the global robot pose.
        val newRobotPosition = robotPosition.exp(update(deltaHeading))
        robotPosition = Pose2d(
            newRobotPosition.translation,
            newHeading + headingOffset,
        )

        prevHeading = newHeading

        // Add the global robot pose to the interpolatable buffer
        localizationBuffer[Timer.getFPGATimestamp().seconds] = robotPosition
    }

    protected abstract fun update(deltaHeading: Rotation2d): Twist2d

    override fun invoke() = robotPosition

    operator fun get(timestamp: SIUnit<Second>) = localizationBuffer[timestamp] ?: Pose2d()

    // Delegates

    operator fun getValue(thisRef: Any?, property: KProperty<*>): Pose2d = robotPosition
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: Pose2d) = reset(value)
}
