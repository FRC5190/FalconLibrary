/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.DriverStation
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.debug.FalconDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.units.inFeet
import org.ghrobotics.lib.utils.Source

class SwerveTrajectoryTrackerCommand(
    private val drivetrain: FalconSwerveDrivetrain,
    private val trajectorySource: Source<Trajectory>,
) : FalconCommand(drivetrain) {

    private var prevStates = Array(4) { SwerveModuleState() }

    private val timer = Timer()
    private var elapsed = 0.0
    private lateinit var trajectory: Trajectory

    override fun initialize() {
        trajectory = trajectorySource()
        timer.start()

        prevStates = Array(4) { SwerveModuleState() }

        FalconDashboard.isFollowingPath = true
    }

    override fun execute() {
        elapsed = timer.get()

        val currentTrajectoryState = trajectory.sample(elapsed)
        val accelerations = Array(4) { 0.0 }

        val chassisSpeeds = drivetrain.controller.calculate(
            drivetrain.robotPosition,
            currentTrajectoryState,
        )

        val wheelStates = drivetrain.kinematics.toSwerveModuleStates(chassisSpeeds)

        drivetrain.setOutputSI(
            wheelStates,
        )

        if (currentTrajectoryState != null) {
            val referencePose = currentTrajectoryState.poseMeters

            FalconDashboard.pathX = referencePose.translation.x_u.inFeet()
            FalconDashboard.pathY = referencePose.translation.y_u.inFeet()
            FalconDashboard.pathHeading = referencePose.rotation.radians
        }
    }

    override fun end(interrupted: Boolean) {
        drivetrain.setNeutral()
        FalconDashboard.isFollowingPath = false

        if (interrupted) {
            DriverStation.reportError("Trajectory tracking was interrupted.", false)
        }
    }

    override fun isFinished() = elapsed > trajectory.totalTimeSeconds
}
