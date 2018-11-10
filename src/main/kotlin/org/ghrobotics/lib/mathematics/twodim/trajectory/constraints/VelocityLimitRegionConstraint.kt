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

package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearVelocity

class VelocityLimitRegionConstraint internal constructor(
    private val region: Rectangle2d,
    private val velocityLimit: Double
) : TimingConstraint<Translation2d> {

    constructor(
        region: Rectangle2d,
        velocityLimit: LinearVelocity
    ) : this(region, velocityLimit.value)

    override fun getMaxVelocity(state: Translation2d) =
        if (state in region) velocityLimit else Double.POSITIVE_INFINITY

    override fun getMinMaxAcceleration(
        state: Translation2d,
        velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}