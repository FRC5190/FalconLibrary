package org.ghrobotics.lib.mathematics.twodim.trajectory.types

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.lerp
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.types.VaryInterpolatable

class TimedTrajectory<S : VaryInterpolatable<S>>(
    points: List<TimedEntry<S>>
) : Trajectory<Time, TimedEntry<S>>(points) {

    override fun sample(interpolant: Time) = sample(interpolant.asMetric.asDouble)

    fun sample(interpolant: Double) = when {
        interpolant >= lastInterpolant.asDouble -> TrajectorySamplePoint(getPoint(points.size - 1))
        interpolant <= firstInterpolant.asDouble -> TrajectorySamplePoint(getPoint(0))
        else -> {
            val (index, entry) = points.asSequence()
                .withIndex()
                .first { (index, entry) -> index != 0 && entry.t >= interpolant }

            val prevEntry = points[index - 1]
            if (entry.t epsilonEquals prevEntry.t) TrajectorySamplePoint(entry, index, index)
            else TrajectorySamplePoint(
                prevEntry.interpolate(
                    entry,
                    (interpolant - prevEntry.t) / (entry.t - prevEntry.t)
                ),
                index - 1,
                index
            )
        }
    }

    override val firstState = points.first()
    override val lastState = points.last()

    override val firstInterpolant = firstState.t.second
    override val lastInterpolant = lastState.t.second

    override fun iterator() = TimedIterator(this)

}

data class TimedEntry<S : VaryInterpolatable<S>>(
    val state: S,
    val t: Double = 0.0,
    val velocity: Double = 0.0,
    val acceleration: Double = 0.0
) : VaryInterpolatable<TimedEntry<S>> {

    override fun interpolate(endValue: TimedEntry<S>, interpolant: Double): TimedEntry<S> {
        val newT = t.lerp(endValue.t, interpolant)
        val deltaT = newT - t
        if (deltaT < 0.0) return endValue.interpolate(this, 1.0 - interpolant)

        val reversing = velocity < 0.0 || velocity epsilonEquals 0.0 && acceleration < 0.0

        val newV = velocity + acceleration * deltaT
        val newS = (if (reversing) -1.0 else 1.0) * (velocity * deltaT + 0.5 * acceleration * deltaT * deltaT)

        return TimedEntry(
            state.interpolate(endValue.state, newS / state.distance(endValue.state)),
            newT,
            newV,
            acceleration
        )
    }

    override fun distance(other: TimedEntry<S>) = state.distance(other.state)

}

class TimedIterator<S : VaryInterpolatable<S>>(
    trajectory: TimedTrajectory<S>
) : TrajectoryIterator<Time, TimedEntry<S>>(trajectory) {
    override fun addition(a: Time, b: Time) = a + b
}

fun TimedTrajectory<Pose2d>.mirror() = TimedTrajectory(
    points.map {
        TimedEntry(
            it.state.mirror,
            it.t,
            it.velocity,
            it.acceleration
        )
    }
)

fun TimedTrajectory<Pose2d>.transform(transform: Pose2d) = TimedTrajectory(
    points.map {
        TimedEntry(
            it.state + transform,
            it.t,
            it.velocity,
            it.acceleration
        )
    }
)