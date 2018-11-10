package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.derivedunits.AngularAcceleration

class AngularAccelerationConstraint internal constructor(
    private val maxAngularAcceleration: Double
) : TimingConstraint<Pose2dWithCurvature> {

    constructor(maxAngularAcceleration: AngularAcceleration) : this(maxAngularAcceleration.value)

    override fun getMaxVelocity(state: Pose2dWithCurvature) = Double.POSITIVE_INFINITY

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TimingConstraint.MinMaxAcceleration {
        // a = alpha * r
        // a * curvature = alpha
        val maxAbsAcceleration = maxAngularAcceleration / state.curvature._curvature
        return TimingConstraint.MinMaxAcceleration(-maxAbsAcceleration, maxAbsAcceleration)
    }
}