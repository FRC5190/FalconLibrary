package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearVelocity

class VelocityLimitRadiusConstraint internal constructor(
    private val point: Translation2d,
    private val radius: Double,
    private val velocityLimit: Double
) : TimingConstraint<Pose2dWithCurvature> {

    constructor(
        point: Translation2d,
        radius: Length,
        velocityLimit: LinearVelocity
    ) : this(point, radius.value, velocityLimit.value)

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        if (state.pose.translation.distance(point) <= radius) velocityLimit else Double.POSITIVE_INFINITY

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}