package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.derivedunits.AngularAcceleration

class AngularAccelerationConstraint(
    private val maxAngularAcceleration: AngularAcceleration
) : TimingConstraint<Pose2dWithCurvature> {

    override fun getMaxVelocity(state: Pose2dWithCurvature) = Double.POSITIVE_INFINITY

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TimingConstraint.MinMaxAcceleration {
        // a = alpha * r
        // a * curvature = alpha
        val maxAbsAcceleration = maxAngularAcceleration.value / state.curvature.curvature.value
        return TimingConstraint.MinMaxAcceleration(-maxAbsAcceleration, maxAbsAcceleration)
    }
}