/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTrackerOutput
import org.ghrobotics.lib.motors.LinearFalconMotor

/**
 * Just implement this if you want to use the TrajectoryTrackerCommand.
 */
interface TrajectoryTrackerDriveBase {
    val leftMotor: LinearFalconMotor
    val rightMotor: LinearFalconMotor

    val trajectoryTracker: TrajectoryTracker

    val robotPosition: Pose2d

    fun setOutput(output: TrajectoryTrackerOutput)

    @JvmDefault
    fun zeroOutputs() {
        leftMotor.setNeutral()
        rightMotor.setNeutral()
    }
}