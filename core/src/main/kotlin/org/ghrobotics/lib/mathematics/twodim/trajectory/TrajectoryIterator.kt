/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

@file:Suppress("unused")

package org.ghrobotics.lib.mathematics.twodim.trajectory

import org.ghrobotics.lib.mathematics.twodim.trajectory.types.Trajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TrajectorySamplePoint
import org.ghrobotics.lib.types.VaryInterpolatable

abstract class TrajectoryIterator<U : Comparable<U>, S : VaryInterpolatable<S>>(
    val trajectory: Trajectory<U, S>
) {

    protected abstract fun addition(a: U, b: U): U

    var progress = trajectory.firstInterpolant
    private var sample = trajectory.sample(progress)

    val isDone get() = progress >= trajectory.lastInterpolant
    val currentState get() = sample

    fun advance(amount: U): TrajectorySamplePoint<S> {
        progress = addition(progress, amount)
            .coerceIn(trajectory.firstInterpolant, trajectory.lastInterpolant)
        sample = trajectory.sample(progress)
        return sample
    }

    fun preview(amount: U): TrajectorySamplePoint<S> {
        val progress = addition(progress, amount)
            .coerceIn(trajectory.firstInterpolant, trajectory.lastInterpolant)
        return trajectory.sample(progress)
    }
}