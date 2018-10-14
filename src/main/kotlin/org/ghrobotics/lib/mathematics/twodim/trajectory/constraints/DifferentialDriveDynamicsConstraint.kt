package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature

class DifferentialDriveDynamicsConstraint(private val drive: DifferentialDrive,
                                          private val maxVoltage: Double) : TimingConstraint<Pose2dWithCurvature> {

    override fun getMaxVelocity(state: Pose2dWithCurvature): Double {
        return drive.getMaxAbsVelocity(state.curvature.curvature, maxVoltage)
    }

    override fun getMinMaxAcceleration(state: Pose2dWithCurvature, velocity: Double): TimingConstraint.MinMaxAcceleration {
        val minMax = drive.getMinMaxAcceleration(
                DifferentialDrive.ChassisState(velocity, velocity * state.curvature.curvature),
                state.curvature.curvature,
                maxVoltage
        )
        return TimingConstraint.MinMaxAcceleration.kNoLimits
    }
}