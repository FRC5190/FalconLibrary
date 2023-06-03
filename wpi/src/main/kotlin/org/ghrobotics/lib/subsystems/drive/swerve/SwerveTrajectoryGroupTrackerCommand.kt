/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.swerve

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.InstantCommand
import org.ghrobotics.lib.PathPlannerTrajectoryStopEventBuilder.stopEventGroup
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.commands.sequential

class SwerveTrajectoryGroupTrackerCommand(
    private val drivetrain: FalconSwerveDrivetrain,
    private val trajectories: List<com.pathplanner.lib.PathPlannerTrajectory>,
    private val eventMap: HashMap<String, Command>,
) : FalconCommand(drivetrain) {
    private lateinit var command: Command

    override fun initialize() {
        command = sequential {
            +InstantCommand({
                drivetrain.resetPosition(trajectories.first().initialHolonomicPose)
            })
            for (trajectory in trajectories) {
                +stopEventGroup(trajectory.startStopEvent, eventMap)
                +drivetrain.followTrajectoryWithCommands(trajectory, eventMap)
            }
            +stopEventGroup(trajectories.last().endStopEvent, eventMap)
        }

        drivetrain.controller.thetaController.reset(drivetrain.robotPosition.rotation.radians)
        command.initialize()
    }

    override fun execute() {
        command.execute()
    }

    override fun end(interrupted: Boolean) {
        command.end(interrupted)
    }

    override fun cancel() {
        super.cancel()
        command.cancel()
    }

    override fun isFinished(): Boolean {
        return command.isFinished
    }
}
