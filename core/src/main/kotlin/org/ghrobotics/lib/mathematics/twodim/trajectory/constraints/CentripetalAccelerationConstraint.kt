/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearAcceleration
import kotlin.math.absoluteValue

class CentripetalAccelerationConstraint internal constructor(
    private val mMaxCentripetalAcceleration: Double
) : TimingConstraint<Pose2dWithCurvature> {

    constructor(mMaxCentripetalAcceleration: LinearAcceleration) : this(mMaxCentripetalAcceleration.value)

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        Math.sqrt((mMaxCentripetalAcceleration / state.curvature).absoluteValue)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}