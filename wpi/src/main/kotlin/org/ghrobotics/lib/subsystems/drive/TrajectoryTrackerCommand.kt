/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.experimental.command.NotifierCommand
import edu.wpi.first.wpilibj.experimental.command.Subsystem
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.utils.Source

/**
 * Command to follow a smooth trajectory using a trajectory following controller
 *
 * @param driveSubsystem Instance of the drive subsystem to use
 * @param trajectorySource Source that contains the trajectory to follow.
 */
class TrajectoryTrackerCommand(
    driveSubsystem: Subsystem,
    private val driveBase: TrajectoryTrackerDriveBase,
    val trajectorySource: Source<Trajectory>,
    private val trajectoryTracker: TrajectoryTracker = driveBase.trajectoryTracker,
    val dt: SIUnit<Second> = 20.milli.seconds
) : NotifierCommand(
    Runnable {
        driveBase.setOutput(trajectoryTracker.nextState(driveBase.robotPosition))

        val referencePoint = trajectoryTracker.referencePoint
        if (referencePoint != null) {
            val referencePose = referencePoint.state.pose

            // Update Current Path Location on Live Dashboard
            LiveDashboard.pathX = referencePose.translation.x_u.feet
            LiveDashboard.pathY = referencePose.translation.y_u.feet
            LiveDashboard.pathHeading = referencePose.rotation.radians
        }
    },
    dt.value,
    driveSubsystem
) {

    /**
     * Reset the trajectory follower with the new trajectory.
     */
    override fun initialize() {
        trajectoryTracker.reset(trajectorySource())
        LiveDashboard.isFollowingPath = true
    }

    /**
     * Make sure that the drivetrain is stopped at the end of the command.
     */
    override fun end(interrupted: Boolean) {
        driveBase.zeroOutputs()
        LiveDashboard.isFollowingPath = false
    }

    override fun isFinished() = trajectoryTracker.isFinished
}
