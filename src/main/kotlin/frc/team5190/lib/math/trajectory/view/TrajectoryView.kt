/*
 * FRC Team 5190
 * Green Hope Falcons
 */


/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.trajectory.view

import frc.team5190.lib.math.geometry.interfaces.State
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectorySamplePoint

interface TrajectoryView<S : State<S>> {

    fun sample(interpolant: Double): TrajectorySamplePoint<S>

    val firstInterpolant: Double
    val lastInterpolant: Double

    val trajectory: Trajectory<S>
}
