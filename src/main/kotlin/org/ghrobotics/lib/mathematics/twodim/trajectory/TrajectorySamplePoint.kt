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

class TrajectorySamplePoint<S : State<S>> {

    val state: S
    private val indexFloor: Int
    private val indexCeil: Int

    constructor(state: S, indexFloor: Int, indexCeil: Int) {
        this.state = state
        this.indexFloor = indexFloor
        this.indexCeil = indexCeil
    }

    constructor(point: TrajectoryPoint<S>) {
        state = point.state()
        indexCeil = point.index()
        indexFloor = indexCeil
    }
}
