/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Translation2d
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.math.trajectory.TrajectoryGenerator
import org.ghrobotics.lib.mathematics.twodim.geometry.mirror

/**
 * Wrapper class over WPILib's trajectory generator that adds
 * support for units.
 */
object FalconTrajectoryGenerator {

    /**
     * Generates a trajectory from the given waypoints and constraints.
     *
     * @param waypoints A list of waypoints.
     * @param config The configuration for the trajectory.
     *
     * @return The trajectory.
     */
    fun generateTrajectory(
        waypoints: List<Pose2d>,
        config: FalconTrajectoryConfig,
    ): Trajectory = TrajectoryGenerator.generateTrajectory(waypoints, config)

    /**
     * Generates a trajectory from the given waypoints and constraints.
     *
     * @param start The starting waypoint.
     * @param interiorWaypoints The interior waypoint translations.
     * @param end The ending waypoint.
     * @param config The configuration for the trajectory.
     *
     * @return The trajectory.
     */
    fun generateTrajectory(
        start: Pose2d,
        interiorWaypoints: List<Translation2d>,
        end: Pose2d,
        config: FalconTrajectoryConfig,
    ): Trajectory = TrajectoryGenerator.generateTrajectory(start, interiorWaypoints, end, config)
}

fun Trajectory.mirror(): Trajectory = Trajectory(
    states.map {
        Trajectory.State(
            it.timeSeconds,
            it.velocityMetersPerSecond,
            it.accelerationMetersPerSecondSq,
            it.poseMeters.mirror(),
            -it.curvatureRadPerMeter,
        )
    },
)
