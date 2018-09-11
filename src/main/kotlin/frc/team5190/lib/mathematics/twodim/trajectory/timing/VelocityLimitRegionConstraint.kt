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

package frc.team5190.lib.mathematics.twodim.trajectory.timing

import frc.team5190.lib.mathematics.twodim.geometry.Rectangle2d
import frc.team5190.lib.mathematics.twodim.geometry.Translation2d
import frc.team5190.lib.mathematics.twodim.geometry.interfaces.ITranslation2d

class VelocityLimitRegionConstraint<S : ITranslation2d<S>>(
        private val region: Rectangle2d,
        private val velocityLimit: Double) : TimingConstraint<S> {

    override fun getMaxVelocity(state: S): Double {
        val translation = state.translation
        return if (translation.x <= region.maxCorner.x && translation.x >= region.minCorner.x &&
                translation.y <= region.maxCorner.y && translation.y >= region.minCorner.y) {
            velocityLimit
        } else java.lang.Double.POSITIVE_INFINITY
    }

    override fun getMinMaxAcceleration(state: S, velocity: Double): TimingConstraint.MinMaxAcceleration {
        return TimingConstraint.MinMaxAcceleration.kNoLimits
    }

}
