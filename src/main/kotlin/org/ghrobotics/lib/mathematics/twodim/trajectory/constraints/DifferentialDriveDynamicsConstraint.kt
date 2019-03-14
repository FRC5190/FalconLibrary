package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.units.derivedunits.Volt

class DifferentialDriveDynamicsConstraint internal constructor(
    private val drive: DifferentialDrive,
    private val maxVoltage: Double
) : TimingConstraint<Pose2dWithCurvature> {

    constructor(
        drive: DifferentialDrive,
        maxVoltage: Volt
    ) : this(drive, maxVoltage.value)

    override fun getMaxVelocity(state: Pose2dWithCurvature) =
        drive.getMaxAbsVelocity(state.curvature, maxVoltage)

    override fun getMinMaxAcceleration(
        state: Pose2dWithCurvature,
        velocity: Double
    ): TimingConstraint.MinMaxAcceleration {
        val minMax = drive.getMinMaxAcceleration(
            DifferentialDrive.ChassisState(velocity, velocity * state.curvature),
            state.curvature,
            maxVoltage
        )
        return TimingConstraint.MinMaxAcceleration(minMax.min, minMax.max)
    }
}