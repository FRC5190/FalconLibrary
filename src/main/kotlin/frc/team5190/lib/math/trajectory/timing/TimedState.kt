/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

@file:Suppress("EqualsOrHashCode")

package frc.team5190.lib.math.trajectory.timing


import frc.team5190.lib.extensions.epsilonEquals
import frc.team5190.lib.math.geometry.interfaces.State
import frc.team5190.lib.types.Interpolable

import java.text.DecimalFormat

open class TimedState<S : State<S>> : State<TimedState<S>> {

    override fun toCSV(): String {
        return ""
    }

    val state: S
    var t: Double = 0.toDouble() // Time we achieve this state.
    var velocity: Double = 0.toDouble() // ds/dt
    var acceleration: Double = 0.toDouble() // d^2s/dt^2

    constructor(state: S) {
        this.state = state
    }

    constructor(state: S, t: Double, velocity: Double, acceleration: Double) {
        this.state = state
        this.t = t
        this.velocity = velocity
        this.acceleration = acceleration
    }


    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return (state.toString() + ", t: " + fmt.format(t) + ", kV: " + fmt.format(velocity) + ", a: "
                + fmt.format(acceleration))
    }


    override fun interpolate(upperVal: TimedState<S>, interpolatePoint: Double): TimedState<S> {
        val newT = Interpolable.interpolate(t, upperVal.t, interpolatePoint)
        val deltaT = newT - t
        if (deltaT < 0.0) {
            return upperVal.interpolate(this, 1.0 - interpolatePoint)
        }
        val reversing = velocity < 0.0 || velocity epsilonEquals 0.0 && acceleration < 0.0

        val newV = velocity + acceleration * deltaT
        val newS = (if (reversing) -1.0 else 1.0) * (velocity * deltaT + .5 * acceleration * deltaT * deltaT)

        return TimedState(state.interpolate(upperVal.state, newS / state.distance(upperVal.state)),
                newT,
                newV,
                acceleration)
    }

    override fun distance(other: TimedState<S>): Double {
        return state.distance(other.state)
    }

    override fun equals(other: Any?): Boolean {
        if (other == null || other !is TimedState<*>) return false
        val ts = other as TimedState<*>?
        return state == ts!!.state && Math.abs(t - ts.t) < 1E-9
    }
}
