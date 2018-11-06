package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature

class AngularAccelerationConstraint(val maxAngularAcceleration: Double) : TimingConstraint<Pose2dWithCurvature> {

    override fun getMaxVelocity(state: Pose2dWithCurvature): Double {
        return Double.POSITIVE_INFINITY
    }

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TimingConstraint.MinMaxAcceleration {
        // a = alpha * r
        // a * curvature = alpha
        val maxAbsAcceleration = maxAngularAcceleration / state.curvature.curvature
        return TimingConstraint.MinMaxAcceleration(-maxAbsAcceleration, maxAbsAcceleration)
    }


}