/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.wpilibj.controller.RamseteController
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

/**
 * An interface to implement to follow trajectories.
 */
abstract class TrajectoryTrackerDriveBase : FalconSubsystem() {

    abstract val controller: RamseteController
    abstract var robotPosition: Pose2d
    abstract val kinematics: DifferentialDriveKinematics

    abstract fun setOutput(
        leftVelocity: SIUnit<LinearVelocity>,
        rightVelocity: SIUnit<LinearVelocity>,
        leftAcceleration: SIUnit<LinearAcceleration>,
        rightAcceleration: SIUnit<LinearAcceleration>
    )
}