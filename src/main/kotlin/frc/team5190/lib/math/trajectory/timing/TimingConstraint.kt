/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.trajectory.timing

import frc.team5190.lib.math.geometry.interfaces.State

interface TimingConstraint<S : State<S>> {

    fun getMaxVelocity(state: S): Double

    fun getMinMaxAcceleration(state: S, velocity: Double): MinMaxAcceleration

    class MinMaxAcceleration {
        val minAcceleration: Double
        val maxAcceleration: Double

        val valid
            get() = minAcceleration <= maxAcceleration

        constructor() {
            // No limits.
            minAcceleration = Double.NEGATIVE_INFINITY
            maxAcceleration = Double.POSITIVE_INFINITY
        }

        @Suppress("unused")
        constructor(min_acceleration: Double, max_acceleration: Double) {
            minAcceleration = min_acceleration
            maxAcceleration = max_acceleration
        }


        companion object {
            var kNoLimits = MinMaxAcceleration()
        }
    }
}
