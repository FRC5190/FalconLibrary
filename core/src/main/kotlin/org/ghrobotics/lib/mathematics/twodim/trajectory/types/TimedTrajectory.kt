/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.types

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectoryIterator
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.operations.times
import org.ghrobotics.lib.types.VaryInterpolatable

class TimedTrajectory<S : VaryInterpolatable<S>>(
    override val points: List<TimedEntry<S>>,
    override val reversed: Boolean
) : Trajectory<SIUnit<Second>, TimedEntry<S>> {

    override fun sample(interpolant: SIUnit<Second>) = when {
        interpolant >= lastInterpolant -> TrajectorySamplePoint(getPoint(points.size - 1))
        interpolant <= firstInterpolant -> TrajectorySamplePoint(getPoint(0))
        else -> {
            val (index, entry) = points.asSequence()
                .withIndex()
                .first { (index, entry) -> index != 0 && entry.t >= interpolant }

            val prevEntry = points[index - 1]
            if (entry.t epsilonEquals prevEntry.t) {
                TrajectorySamplePoint(entry, index, index)
            } else {
                TrajectorySamplePoint(
                    prevEntry.interpolate(
                        entry,
                        ((interpolant - prevEntry.t) / (entry.t - prevEntry.t)).value
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

data class TimedEntry<S : VaryInterpolatable<S>> constructor(
    val state: S,
    val t: SIUnit<Second> = SIUnit(0.0),
    val velocity: SIUnit<LinearVelocity> = SIUnit(0.0),
    val acceleration: SIUnit<LinearAcceleration> = SIUnit(0.0)
) : VaryInterpolatable<TimedEntry<S>> {

    override fun interpolate(endValue: TimedEntry<S>, t: Double): TimedEntry<S> {
        val newT = this.t.lerp(endValue.t, t)
        val deltaT = newT - this.t
        if (deltaT.value < 0.0) return endValue.interpolate(this, 1.0 - t)

        val reversing =
            this.velocity.value < 0.0 || this.velocity.value epsilonEquals 0.0 && this.acceleration.value < 0.0

        val newV = this.velocity + this.acceleration * deltaT
        val newS = (if (reversing) -1.0 else 1.0) * (this.velocity * deltaT + 0.5 * this.acceleration * deltaT * deltaT)

        return TimedEntry(
            state.interpolate(endValue.state, (newS / state.distance(endValue.state)).value),
            newT,
            newV,
            this.acceleration
        )
    }

    override fun distance(other: TimedEntry<S>) = state.distance(other.state)
}

class TimedIterator<S : VaryInterpolatable<S>>(
    trajectory: TimedTrajectory<S>
) : TrajectoryIterator<SIUnit<Second>, TimedEntry<S>>(trajectory) {
    override fun addition(a: SIUnit<Second>, b: SIUnit<Second>) = a + b
}

fun Trajectory<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>.mirror() =
    TimedTrajectory(points.map { TimedEntry(it.state.mirror, it.t, it.velocity, it.acceleration) }, this.reversed)

fun Trajectory<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>.transform(transform: Pose2d) =
    TimedTrajectory(
        points.map { TimedEntry(it.state + transform, it.t, it.velocity, it.acceleration) },
        this.reversed
    )

val Trajectory<SIUnit<Second>, TimedEntry<Pose2dWithCurvature>>.duration get() = this.lastState.t