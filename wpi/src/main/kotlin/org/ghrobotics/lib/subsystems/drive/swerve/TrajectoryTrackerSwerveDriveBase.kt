/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.swerve

import edu.wpi.first.math.controller.HolonomicDriveController
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModuleState
import org.ghrobotics.lib.commands.FalconSubsystem

abstract class TrajectoryTrackerSwerveDriveBase : FalconSubsystem() {
    abstract val controller: HolonomicDriveController
    abstract var robotPosition: Pose2d
    abstract val kinematics: SwerveDriveKinematics

    abstract fun setOutputSI(
        states: Array<SwerveModuleState>,
    )
}
