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
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.meter

class VelocityLimitRegionConstraint(
        val region: Rectangle2d,
        val velocityLimitRaw: Double
) : TimingConstraint<Translation2d> {

    val velocityLimit
        get() = velocityLimitRaw.meter.velocity

    constructor(
            region: Rectangle2d,
            velocityLimit: Velocity
    ) : this(
            region,
            velocityLimit.asMetric.asDouble
    )

    override fun getMaxVelocity(state: Translation2d) =
            if (state in region) velocityLimitRaw else Double.POSITIVE_INFINITY

    override fun getMinMaxAcceleration(
            state: Translation2d,
            velocity: Double
    ) = TimingConstraint.MinMaxAcceleration.kNoLimits
}