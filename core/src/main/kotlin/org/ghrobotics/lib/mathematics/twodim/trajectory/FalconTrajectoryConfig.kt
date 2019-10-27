/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.trajectory.TrajectoryConfig
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

/**
 * Unit-safe wrapper for TrajectoryConfig, a configuration class
 * used to generate trajectories.
 *
 * @param maxVelocity The maximum velocity of the trajectory.
 * @param maxAcceleration The maximum acceleration of the trajectory.
 */
@Suppress("MemberVisibilityCanBePrivate")
class FalconTrajectoryConfig(
    val maxVelocity: SIUnit<LinearVelocity>,
    val maxAcceleration: SIUnit<LinearAcceleration>
) : TrajectoryConfig(maxVelocity.value, maxAcceleration.value) {

    /**
     * Get the starting velocity of the trajectory.
     */
    val startVelocity get() = SIUnit<LinearVelocity>(super.getStartVelocityMetersPerSecond())

    /**
     * Get the ending velocity of the trajectory.
     */
    val endVelocity get() = SIUnit<LinearVelocity>(super.getEndVelocityMetersPerSecond())

    /**
     * Set the starting velocity of the trajectory.
     * @param startVelocity The start velocity of the trajectory.
     */
    fun setStartVelocity(startVelocity: SIUnit<LinearVelocity>): FalconTrajectoryConfig = also {
        super.setStartVelocity(startVelocity.value)
    }

    /**
     * Set the ending velocity of the trajectory.
     * @param endVelocity The ending velocity of the trajectory.
     */
    fun setEndVelocity(endVelocity: SIUnit<LinearVelocity>): FalconTrajectoryConfig = also {
        super.setEndVelocity(endVelocity.value)
    }

    override fun addConstraint(constraint: TrajectoryConstraint): FalconTrajectoryConfig = also {
        super.addConstraint(constraint)
    }

    override fun addConstraints(constraints: List<TrajectoryConstraint>): FalconTrajectoryConfig = also {
        super.addConstraints(constraints)
    }

    fun addConstraints(vararg constraints: TrajectoryConstraint): FalconTrajectoryConfig = also {
        super.addConstraints(constraints.asList())
    }
}
