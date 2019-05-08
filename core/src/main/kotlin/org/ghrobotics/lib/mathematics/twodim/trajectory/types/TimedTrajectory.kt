package org.ghrobotics.lib.mathematics.twodim.trajectory.types

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.lerp
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.types.VaryInterpolatable

class TimedTrajectory<S : VaryInterpolatable<S>>(
    override val points: List<TimedEntry<S>>,
    override val reversed: Boolean
) : Trajectory<Time, TimedEntry<S>> {

    override fun sample(interpolant: Time) = sample(interpolant.value)

    fun sample(interpolant: Double) = when {
        interpolant >= lastInterpolant.value -> TrajectorySamplePoint(getPoint(points.size - 1))
        interpolant <= firstInterpolant.value -> TrajectorySamplePoint(getPoint(0))
        else -> {
            val (index, entry) = points.asSequence()
                .withIndex()
                .first { (index, entry) -> index != 0 && entry.t.value >= interpolant }

            val prevEntry = points[index - 1]
            if (entry.t epsilonEquals prevEntry.t) {
                TrajectorySamplePoint(entry, index, index)
            } else {
                TrajectorySamplePoint(
                    prevEntry.interpolate(
                        entry,
                        (interpolant - prevEntry.t.value) / (entry.t.value - prevEntry.t.value)
                    ),
                    index - 1,
                    index
                )
            }
        }
    }

    override val firstState get() = points.first()
    override val lastState get() = points.last()

    override val firstInterpolant get() = firstState.t
    override val lastInterpolant get() = lastState.t

    override fun iterator() = TimedIterator(this)
}

data class TimedEntry<S : VaryInterpolatable<S>> internal constructor(
    val state: S,
    internal val _t: Double = 0.0,
    internal val _velocity: Double = 0.0,
    internal val _acceleration: Double = 0.0
) : VaryInterpolatable<TimedEntry<S>> {

    val t get() = _t.second
    val velocity get() = _velocity.meter.velocity
    val acceleration get() = _acceleration.meter.acceleration

    constructor(
        state: S,
        t: Time,
        velocity: LinearVelocity,
        acceleration: LinearAcceleration
    ) : this(state, t.value, velocity.value, acceleration.value)

    override fun interpolate(endValue: TimedEntry<S>, t: Double): TimedEntry<S> {
        val newT = _t.lerp(endValue._t, t)
        val deltaT = newT - this.t.value
        if (deltaT < 0.0) return endValue.interpolate(this, 1.0 - t)

        val reversing = _velocity < 0.0 || _velocity epsilonEquals 0.0 && _acceleration < 0.0

        val newV = _velocity + _acceleration * deltaT
        val newS = (if (reversing) -1.0 else 1.0) * (_velocity * deltaT + 0.5 * _acceleration * deltaT * deltaT)

        return TimedEntry(
            state.interpolate(endValue.state, newS / state.distance(endValue.state)),
            newT,
            newV,
            _acceleration
        )
    }

    override fun distance(other: TimedEntry<S>) = state.distance(other.state)
}

class TimedIterator<S : VaryInterpolatable<S>>(
    trajectory: TimedTrajectory<S>
) : TrajectoryIterator<Time, TimedEntry<S>>(trajectory) {
    override fun addition(a: Time, b: Time) = a + b
}

fun Trajectory<Time, TimedEntry<Pose2dWithCurvature>>.mirror() =
    TimedTrajectory(points.map { TimedEntry(it.state.mirror, it._t, it._velocity, it._acceleration) }, this.reversed)

fun Trajectory<Time, TimedEntry<Pose2dWithCurvature>>.transform(transform: Pose2d) =
    TimedTrajectory(
        points.map { TimedEntry(it.state + transform, it._t, it._velocity, it._acceleration) },
        this.reversed
    )

val Trajectory<Time, TimedEntry<Pose2dWithCurvature>>.duration get() = this.lastState.t