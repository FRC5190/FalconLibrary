/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.geometry.Pose2d
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTrackerOutput

/**
 * An interface to implement to follow trajectories.
 */
abstract class TrajectoryTrackerDriveBase : FalconSubsystem() {

    abstract val trajectoryTracker: TrajectoryTracker
    abstract var robotPosition: Pose2d

    abstract fun setOutput(output: TrajectoryTrackerOutput)
}