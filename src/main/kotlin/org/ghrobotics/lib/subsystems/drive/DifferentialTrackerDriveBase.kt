package org.ghrobotics.lib.subsystems.drive

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.meter

interface DifferentialTrackerDriveBase : TrajectoryTrackerDriveBase {

    val differentialDrive: DifferentialDrive

    @JvmDefault
    override fun setOutput(output: TrajectoryTrackerOutput) {
        setOutputFromDynamics(
            output.differentialDriveVelocity,
            output.differentialDriveAcceleration
        )
    }

    @JvmDefault
    fun setOutputFromKinematics(chassisVelocity: DifferentialDrive.ChassisState) {
        val wheelVelocities = differentialDrive.solveInverseKinematics(chassisVelocity)
        val feedForwardVoltages = differentialDrive.getVoltagesFromkV(wheelVelocities)

        setOutput(wheelVelocities, feedForwardVoltages)
    }

    @JvmDefault
    fun setOutputFromDynamics(
        chassisVelocity: DifferentialDrive.ChassisState,
        chassisAcceleration: DifferentialDrive.ChassisState
    ) {
        val dynamics = differentialDrive.solveInverseDynamics(chassisVelocity, chassisAcceleration)

        setOutput(dynamics.wheelVelocity, dynamics.voltage)
    }

    @JvmDefault
    fun setOutput(
        wheelVelocities: DifferentialDrive.WheelState,
        wheelVoltages: DifferentialDrive.WheelState
    ) {
        leftMotor.setVelocityAndArbitraryFeedForward(
            (wheelVelocities.left * differentialDrive.wheelRadius).meter.velocity,
            wheelVoltages.left / 12.0
        )
        rightMotor.setVelocityAndArbitraryFeedForward(
            (wheelVelocities.right * differentialDrive.wheelRadius).meter.velocity,
            wheelVoltages.right / 12.0
        )
    }
}