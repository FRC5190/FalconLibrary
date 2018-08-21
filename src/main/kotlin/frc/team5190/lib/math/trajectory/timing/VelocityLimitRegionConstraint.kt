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

package frc.team5190.lib.math.trajectory.timing

import frc.team5190.lib.math.geometry.Translation2d
import frc.team5190.lib.math.geometry.interfaces.ITranslation2d

class VelocityLimitRegionConstraint<S : ITranslation2d<S>>(
        private val minCorner: Translation2d,
        private val maxCorner: Translation2d,
        private val velocityLimit: Double) : TimingConstraint<S> {

    override fun getMaxVelocity(state: S): Double {
        val translation = state.translation
        return if (translation.x <= maxCorner.x && translation.x >= minCorner.x &&
                translation.y <= maxCorner.y && translation.y >= minCorner.y) {
            velocityLimit
        } else java.lang.Double.POSITIVE_INFINITY
    }

    override fun getMinMaxAcceleration(state: S, velocity: Double): TimingConstraint.MinMaxAcceleration {
        return TimingConstraint.MinMaxAcceleration.kNoLimits
    }

}
