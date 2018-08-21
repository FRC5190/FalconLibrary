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

import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.math.geometry.interfaces.State
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectorySamplePoint

class DistanceView<S : State<S>>(override val trajectory: Trajectory<S>) : TrajectoryView<S> {


    private val distances: DoubleArray = DoubleArray(trajectory.length)

    init {
        distances[0] = 0.0
        for (i in 1 until trajectory.length) {
            distances[i] = distances[i - 1] + trajectory.getState(i - 1).distance(trajectory.getState(i))
        }
    }

    override fun sample(interpolant: Double): TrajectorySamplePoint<S> {
        if (interpolant >= lastInterpolant)
            return TrajectorySamplePoint(trajectory.getPoint(trajectory.length - 1))
        if (interpolant <= 0.0)
            return TrajectorySamplePoint(trajectory.getPoint(0))
        for (i in 1 until distances.size) {
            val s = trajectory.getPoint(i)
            if (distances[i] >= interpolant) {
                val prevS = trajectory.getPoint(i - 1)
                return if (distances[i] epsilonEquals distances[i - 1]) {
                    TrajectorySamplePoint(s)
                } else {
                    TrajectorySamplePoint(prevS.state().interpolate(s.state(),
                            (interpolant - distances[i - 1]) / (distances[i] - distances[i - 1])), i - 1, i)
                }
            }
        }
        throw RuntimeException()
    }

    override val lastInterpolant get() = distances[distances.size - 1]
    override val firstInterpolant get() = 0.0

}
