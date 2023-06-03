/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.westcoast

import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import edu.wpi.first.wpilibj2.command.Command
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.debug.FalconDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.units.inFeet
import org.ghrobotics.lib.utils.Source

/**
 * Represents a command that is used to follow a trajectory.
 *
 * @param drivetrain The drivetrain being used to follow the trajectory.
 * @param trajectorySource The trajectory source.
 */
class WestCoastTrajectoryTrackerCommand(
    private val drivetrain: TrajectoryTrackerWestCoastDriveBase,
    private val trajectorySource: Source<Trajectory>,
    private val eventMap: Map<String, Command> = HashMap(),
) : FalconCommand(drivetrain) {

    private var prevLeftVelocity = 0.0
    private var prevRightVelocity = 0.0

    private val timer = Timer()
    private var elapsed = 0.0
    private lateinit var trajectory: Trajectory

    /**
     * Initializes the command,
     */
    override fun initialize() {
        trajectory = trajectorySource()
        timer.start()

        prevLeftVelocity = 0.0
        prevRightVelocity = 0.0

        FalconDashboard.isFollowingPath = true
    }

    /**
     * Executes at 50 Hz.
     */
    override fun execute() {
        // Get the elapsed time
        elapsed = timer.get()

        // Get the current trajectory state.
        val currentTrajectoryState = trajectory.sample(elapsed)

        // Get the adjusted chassis speeds from the controller.
        val chassisSpeeds = drivetrain.controller.calculate(
            drivetrain.robotPosition,
            currentTrajectoryState,
        )

        // Get the wheel speeds from the chassis speeds.
        val wheelSpeeds = drivetrain.kinematics.toWheelSpeeds(chassisSpeeds)

        // Calculate accelerations
        val leftAcceleration = (wheelSpeeds.leftMetersPerSecond - prevLeftVelocity) * 50
        val rightAcceleration = (wheelSpeeds.rightMetersPerSecond - prevRightVelocity) * 50

        prevLeftVelocity = wheelSpeeds.leftMetersPerSecond
        prevRightVelocity = wheelSpeeds.rightMetersPerSecond

        drivetrain.setOutputSI(
            wheelSpeeds.leftMetersPerSecond,
            wheelSpeeds.rightMetersPerSecond,
            leftAcceleration,
            rightAcceleration,
        )

        if (currentTrajectoryState != null) {
            val referencePose = currentTrajectoryState.poseMeters

            // Update Current Path Location on Live Dashboard
            FalconDashboard.pathX = referencePose.translation.x_u.inFeet()
            FalconDashboard.pathY = referencePose.translation.y_u.inFeet()
            FalconDashboard.pathHeading = referencePose.rotation.radians
        }
    }

    /**
     * Ends the command.
     */
    override fun end(interrupted: Boolean) {
        drivetrain.setNeutral()
        FalconDashboard.isFollowingPath = false

        if (interrupted) {
            DriverStation.reportError("Trajectory tracking was interrupted.", false)
        }
    }

    /**
     * Checks if the trajectory has finished executing.
     */
    override fun isFinished() = elapsed > trajectory.totalTimeSeconds
}
