/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.derived.LinearAcceleration
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.operations.times
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.mathematics.units.unitlessValue

class Trajectory(private val timedStates: List<TimedState>, val reversed: Boolean) {
    internal val lastInterpolant = timedStates.last().t

    var currentState = timedStates.first()
        private set

    var progress = 0.seconds
        private set

    val isDone: Boolean
        get() = progress > lastInterpolant

    val remainingProgress
        get() = lastInterpolant - progress

    data class TimedState internal constructor(
        val t: SIUnit<Second> = SIUnit(0.0),
        val velocity: SIUnit<LinearVelocity> = SIUnit(0.0),
        val acceleration: SIUnit<LinearAcceleration> = SIUnit(0.0),
        val state: Pose2dWithCurvature
    ) {
        fun interpolate(endValue: TimedState, t: Double): TimedState {
            val newT = this.t.lerp(endValue.t, t)
            val deltaT = newT - this.t
            if (deltaT.value < 0.0) return endValue.interpolate(this, 1.0 - t)

            val reversing =
                this.velocity.value < 0.0 || this.velocity.value epsilonEquals 0.0 && this.velocity.value < 0.0

            val newV = this.velocity + (this.acceleration * deltaT)
            val newS =
                (if (reversing) -1.0 else 1.0) * (this.velocity * deltaT + 0.5 * this.acceleration * deltaT * deltaT)

            return TimedState(
                newT,
                newV,
                this.acceleration,
                state.interpolate(endValue.state, (newS / state.distance(endValue.state)).value)
            )
        }
    }


    private fun sample(t: SIUnit<Second>): TimedState {
        return if (t <= timedStates.first().t) timedStates.first()
        else if (t >= timedStates.last().t) timedStates.last()
        else {
            val (index, entry) = timedStates.asSequence()
                .withIndex()
                .first { (index, entry) -> index != 0 && entry.t >= t }

            val prevEntry = timedStates[index - 1]
            if (entry.t epsilonEquals prevEntry.t) {
                entry
            } else {
                prevEntry.interpolate(entry, ((t - prevEntry.t) / (entry.t - prevEntry.t)).unitlessValue)
            }
        }
    }

    fun advance(t: SIUnit<Second>): TimedState {
        progress += t
        currentState = sample(progress)
        return currentState
    }

    fun preview(t: SIUnit<Second>): TimedState {
        return sample(progress + t)
    }

    fun mirror(): Trajectory {
        return Trajectory(
            timedStates.map { TimedState(it.t, it.velocity, it.acceleration, it.state.mirror()) },
            this.reversed
        )
    }
}