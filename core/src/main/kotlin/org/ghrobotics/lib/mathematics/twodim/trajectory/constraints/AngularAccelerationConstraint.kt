package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.derivedunits.AngularAcceleration
import kotlin.math.absoluteValue

class AngularAccelerationConstraint internal constructor(
    private val maxAngularAcceleration: Double
) : TimingConstraint<Pose2dWithCurvature> {

    constructor(maxAngularAcceleration: AngularAcceleration) : this(maxAngularAcceleration.value)

    init {
        require(maxAngularAcceleration >= 0) { "Cannot have negative Angular Acceleration." }
    }

    override fun getMaxVelocity(state: Pose2dWithCurvature): Double {
        /**
         * We don't want v^2 * dk/ds alone to go over the max angular acceleration.
         * v^2 * dk/ds = maxAngularAcceleration when linear acceleration = 0.
         * v = sqrt(maxAngularAcceleration / dk/ds)
         */

        return Math.sqrt(maxAngularAcceleration / state.dkds.absoluteValue)
    }

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TimingConstraint.MinMaxAcceleration {

        /**
         * We want to limit the acceleration such that we never go above the specified angular acceleration.
         *
         * Angular acceleration = dw/dt     WHERE   w = omega = angular velocity
         * w = v * k                        WHERE   v = linear velocity, k = curvature
         *
         * dw/dt = d/dt (v * k)
         *
         * By chain rule,
         * dw/dt = dv/dt * k + v * dk/dt   [1]
         *
         * We don't have dk/dt, but we do have dk/ds and ds/dt
         * dk/ds * ds/dt = dk/dt     [2]
         *
         * Substituting [2] in [1], we get
         * dw/dt = acceleration * curvature + velocity * velocity * d_curvature
         * WHERE acceleration = dv/dt, velocity = ds/dt, d_curvature = dk/dt and curvature = k
         *
         * We now want to find the linear acceleration such that the angular acceleration (dw/dt) never goes above
         * the specified amount.
         *
         * acceleration * curvature = dw/dt - (velocity * velocity * d_curvature)
         * acceleration = (dw/dt - (velocity * velocity * d_curvature)) / curvature
         *
         * Yay Calculus
         */

        val maxAbsoluteAcceleration = Math.abs(
            (maxAngularAcceleration - (velocity * velocity * state.dkds)) / state.curvature
        )

        return TimingConstraint.MinMaxAcceleration(-maxAbsoluteAcceleration, maxAbsoluteAcceleration)
    }
}