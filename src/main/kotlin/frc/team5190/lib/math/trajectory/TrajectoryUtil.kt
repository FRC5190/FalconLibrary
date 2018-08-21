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

package frc.team5190.lib.math.trajectory

import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.interfaces.IPose2d
import frc.team5190.lib.math.geometry.interfaces.State
import frc.team5190.lib.kEpsilon
import frc.team5190.lib.math.spline.QuinticHermiteSpline
import frc.team5190.lib.math.spline.Spline
import frc.team5190.lib.math.spline.SplineGenerator
import frc.team5190.lib.math.trajectory.timing.TimedState
import frc.team5190.lib.math.trajectory.view.TrajectoryView
import java.util.*

object TrajectoryUtil {

    fun <S : IPose2d<S>> mirror(trajectory: Trajectory<S>): Trajectory<S> {
        val waypoints = ArrayList<S>(trajectory.length)
        for (i in 0 until trajectory.length) {
            waypoints.add(trajectory.getState(i).mirror)
        }
        return Trajectory(waypoints)
    }

    fun <S : IPose2d<S>> mirrorTimed(trajectory: Trajectory<TimedState<S>>): Trajectory<TimedState<S>> {
        val waypoints = ArrayList<TimedState<S>>(trajectory.length)
        for (i in 0 until trajectory.length) {
            val timedState = trajectory.getState(i)
            waypoints.add(TimedState(timedState.state.mirror, timedState.t, timedState.velocity, timedState.acceleration))
        }
        return Trajectory(waypoints)
    }

    fun <S : IPose2d<S>> transform(trajectory: Trajectory<S>, transform: Pose2d): Trajectory<S> {
        val waypoints = ArrayList<S>(trajectory.length)
        for (i in 0 until trajectory.length) {
            waypoints.add(trajectory.getState(i).transformBy(transform))
        }
        return Trajectory(waypoints)
    }

    fun <S : State<S>> resample(
            trajectory_view: TrajectoryView<S>, interval: Double): Trajectory<S> {
        if (interval <= kEpsilon) {
            return Trajectory()
        }
        val numStates = Math
                .ceil((trajectory_view.lastInterpolant - trajectory_view.firstInterpolant) / interval).toInt()
        val states = ArrayList<S>(numStates)

        for (i in 0 until numStates) {
            states.add(trajectory_view.sample(i * interval + trajectory_view.firstInterpolant).state)
        }
        return Trajectory(states)
    }

    fun trajectoryFromSplineWaypoints(waypoints: List<Pose2d>, maxDx: Double, maxDy: Double, maxDTheta: Double): Trajectory<Pose2dWithCurvature> {
        val splines = ArrayList<QuinticHermiteSpline>(waypoints.size - 1)
        for (i in 1 until waypoints.size) {
            splines.add(QuinticHermiteSpline(waypoints[i - 1], waypoints[i]))
        }
        QuinticHermiteSpline.optimizeSpline(splines)
        return trajectoryFromSplines(splines, maxDx, maxDy, maxDTheta)
    }

    private fun trajectoryFromSplines(splines: List<Spline>, maxDx: Double,
                                      maxDy: Double, maxDTheta: Double): Trajectory<Pose2dWithCurvature> {
        return Trajectory(SplineGenerator.parameterizeSplines(splines, maxDx, maxDy,
                maxDTheta))
    }
}
