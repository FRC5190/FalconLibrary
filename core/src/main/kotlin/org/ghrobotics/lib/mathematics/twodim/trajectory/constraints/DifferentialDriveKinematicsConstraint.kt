/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

/**
 * Class that imposes constraints on the trajectory's max velocity based on the
 * kinematics of the differential drive system.
 *
 * @param kinematics The kinematics that model the drivetrain.
 * @param maxAbsoluteSpeed The max absolute speed for a wheel on the drivetrain.
 */
class DifferentialDriveKinematicsConstraint(
    private val kinematics: DifferentialDriveKinematics,
    private val maxAbsoluteSpeed: SIUnit<LinearVelocity>
) : TrajectoryConstraint {

    /**
     * Returns the max velocity given the state and the velocity of the trajectory at that point.
     */
    override fun getMaxVelocity(state: Pose2dWithCurvature, velocity: Double): Double {
        val wheelSpeeds = kinematics.toWheelSpeeds(ChassisSpeeds(velocity, 0.0, velocity * state.curvature))
        wheelSpeeds.normalize(maxAbsoluteSpeed.value)

        return (kinematics.toChassisSpeeds(wheelSpeeds)).vxMetersPerSecond
    }

    /**
     * This constraint has nothing to do with the acceleration.
     */
    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TrajectoryConstraint.MinMaxAcceleration {
        return TrajectoryConstraint.MinMaxAcceleration()
    }
}