/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package org.ghrobotics.lib.mathematics.twodim.trajectory

import org.ghrobotics.lib.mathematics.twodim.geometry.interfaces.State

class TrajectoryPoint<S : State<S>>(val state: S, private val index: Int) {

    fun state(): S {
        return state
    }

    fun index(): Int {
        return index
    }
}
