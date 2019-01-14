package org.ghrobotics.lib.mathematics.twodim.trajectory.types

import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.types.VaryInterpolatable

interface Trajectory<U : Comparable<U>, S : VaryInterpolatable<S>> {
    val points: List<S>
    val reversed get() = false

    @JvmDefault
    fun getPoint(index: Int) = TrajectoryPoint(index, points[index])

    fun sample(interpolant: U): TrajectorySamplePoint<S>

    val firstInterpolant: U
    val lastInterpolant: U

    val firstState: S
    val lastState: S

    operator fun iterator(): TrajectoryIterator<U, S>
}

data class TrajectoryPoint<S>(
    val index: Int,
    val state: S
)

data class TrajectorySamplePoint<S>(
    val state: S,
    val indexFloor: Int,
    val indexCeil: Int
) {
    constructor(point: TrajectoryPoint<S>) :
        this(point.state, point.index, point.index)
}