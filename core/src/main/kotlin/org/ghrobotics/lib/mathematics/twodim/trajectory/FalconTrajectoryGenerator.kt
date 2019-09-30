/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import edu.wpi.first.wpilibj.trajectory.Trajectory
import edu.wpi.first.wpilibj.trajectory.TrajectoryGenerator
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint
import org.ghrobotics.lib.mathematics.twodim.geometry.mirror
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity

/**
 * Wrapper class over WPILib's trajectory generator that adds
 * support for units.
 */
object FalconTrajectoryGenerator {

    /**
     * Generates a trajectory from the given waypoints and constraints.
     *
     * @param waypoints A list of waypoints.
     * @param constraints A list of user-defined constraints on velocity and acceleration.
     * @param startVelocity The start velocity.
     * @param endVelocity The end velocity.
     * @param maxVelocity The max velocity.
     * @param maxAcceleration The max acceleration.
     * @param reversed Whether the path is reversed.
     *
     * @return The trajectory.
     */
    fun generateTrajectory(
        waypoints: List<Pose2d>,
        constraints: List<TrajectoryConstraint>,
        startVelocity: SIUnit<LinearVelocity>,
        endVelocity: SIUnit<LinearVelocity>,
        maxVelocity: SIUnit<LinearVelocity>,
        maxAcceleration: SIUnit<LinearAcceleration>,
        reversed: Boolean
    ) = TrajectoryGenerator.generateTrajectory(
        waypoints, constraints,
        startVelocity.value, endVelocity.value,
        maxVelocity.value, maxAcceleration.value,
        reversed
    )

    /**
     * Generates a trajectory from the given waypoints and constraints.
     *
     * @param start The starting waypoint.
     * @param interiorWaypoints The interior waypoint translations.
     * @param end The ending waypoint.
     * @param constraints A list of user-defined constraints on velocity and acceleration.
     * @param startVelocity The start velocity.
     * @param endVelocity The end velocity.
     * @param maxVelocity The max velocity.
     * @param maxAcceleration The max acceleration.
     * @param reversed Whether the path is reversed.
     *
     * @return The trajectory.
     */
    fun generateTrajectory(
        start: Pose2d,
        interiorWaypoints: List<Translation2d>,
        end: Pose2d,
        constraints: List<TrajectoryConstraint>,
        startVelocity: SIUnit<LinearVelocity>,
        endVelocity: SIUnit<LinearVelocity>,
        maxVelocity: SIUnit<LinearVelocity>,
        maxAcceleration: SIUnit<LinearAcceleration>,
        reversed: Boolean
    ) = TrajectoryGenerator.generateTrajectory(
        start, interiorWaypoints, end, constraints,
        startVelocity.value, endVelocity.value,
        maxVelocity.value, maxAcceleration.value,
        reversed
    )
}

fun Trajectory.mirror(): Trajectory = Trajectory(
    states.map {
        Trajectory.State(
            it.timeSeconds,
            it.velocityMetersPerSecond,
            it.accelerationMetersPerSecondSq,
            it.poseMeters.mirror(),
            -it.curvatureRadPerMeter
        )
    }
)