/*
 * FRC Team 5190
 * Green Hope Falcons
 */


/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package org.ghrobotics.lib.mathematics.twodim.trajectory.view

import org.ghrobotics.lib.mathematics.State
import org.ghrobotics.lib.mathematics.twodim.trajectory.Trajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectorySamplePoint

interface TrajectoryView<S : State<S>> {

    fun sample(interpolant: Double): TrajectorySamplePoint<S>

    val firstInterpolant: Double
    val lastInterpolant: Double

    val trajectory: Trajectory<S>
}
