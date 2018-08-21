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

package frc.team5190.lib.math.trajectory.view


import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.math.geometry.interfaces.State
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectorySamplePoint
import frc.team5190.lib.math.trajectory.timing.TimedState

class TimedView<S : State<S>>(override val trajectory: Trajectory<TimedState<S>>) : TrajectoryView<TimedState<S>> {

    private val startT: Double = trajectory.getState(0).t
    private val endT: Double = trajectory.getState(trajectory.length - 1).t

    override val firstInterpolant: Double
        get() = startT

    override val lastInterpolant: Double
        get() =  endT


    override fun sample(interpolant: Double): TrajectorySamplePoint<TimedState<S>> {
        if (interpolant >= endT) {
            return TrajectorySamplePoint(trajectory.getPoint(trajectory.length - 1))
        }
        if (interpolant <= startT) {
            return TrajectorySamplePoint(trajectory.getPoint(0))
        }
        for (i in 1 until trajectory.length) {
            val s = trajectory.getPoint(i)

            if (s.state().t >= interpolant) {
                val prevS = trajectory.getPoint(i - 1)
                return if (s.state().t epsilonEquals prevS.state().t) {
                    TrajectorySamplePoint(s)
                } else TrajectorySamplePoint(prevS.state().interpolate(s.state(),
                        (interpolant - prevS.state().t) / (s.state().t - prevS.state().t)), i - 1, i)
            }
        }
        throw RuntimeException()
    }
}
