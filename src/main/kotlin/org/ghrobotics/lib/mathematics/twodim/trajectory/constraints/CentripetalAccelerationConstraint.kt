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

class CentripetalAccelerationConstraint(
    private val mMaxCentripetalAcceleration: LinearAcceleration
) : TimingConstraint<Pose2dWithCurvature> {

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        Math.sqrt((mMaxCentripetalAcceleration.value / state.curvature.curvature.value).absoluteValue)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}