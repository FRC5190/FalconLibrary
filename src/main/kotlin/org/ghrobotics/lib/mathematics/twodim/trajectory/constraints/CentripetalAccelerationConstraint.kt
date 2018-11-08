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
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.meter
import kotlin.math.absoluteValue

class CentripetalAccelerationConstraint(
    val mMaxCentripetalAccelRaw: Double
) : TimingConstraint<Pose2dWithCurvature> {

    val mMaxCentripetalAccel
        get() = mMaxCentripetalAccelRaw.meter.acceleration

    constructor(mMaxCentripetalAccel: LinearAcceleration) :
            this(mMaxCentripetalAccel.value)

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
            Math.sqrt((mMaxCentripetalAccelRaw / state.curvature.curvature).absoluteValue)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}