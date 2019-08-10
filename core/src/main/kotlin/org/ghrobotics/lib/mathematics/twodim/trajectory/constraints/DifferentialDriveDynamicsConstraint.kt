/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Volt

class DifferentialDriveDynamicsConstraint constructor(
    val drive: DifferentialDrive,
    val maxVoltage: SIUnit<Volt>
) : TrajectoryConstraint {

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        drive.getMaxAbsVelocity(state.curvature, maxVoltage.value)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TrajectoryConstraint.MinMaxAcceleration {
        val minMax = drive.getMinMaxAcceleration(
            DifferentialDrive.ChassisState(velocity, velocity * state.curvature),
            state.curvature,
            maxVoltage.value
        )
        return TrajectoryConstraint.MinMaxAcceleration(minMax.min, minMax.max)
    }
}