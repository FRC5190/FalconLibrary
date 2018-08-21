/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.trajectory

import frc.team5190.lib.math.geometry.interfaces.State

class TrajectoryPoint<S : State<S>>(val state: S, private val index: Int) {

    fun state(): S {
        return state
    }

    fun index(): Int {
        return index
    }
}
