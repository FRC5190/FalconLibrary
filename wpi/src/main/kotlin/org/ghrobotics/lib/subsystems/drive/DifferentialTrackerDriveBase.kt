/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTrackerOutput
import org.ghrobotics.lib.mathematics.units.derived.velocity
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.meters

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
        leftMotor.setVelocity(
            (wheelVelocities.left * differentialDrive.wheelRadius).meters.velocity,
            wheelVoltages.left.volts
        )
        rightMotor.setVelocity(
            (wheelVelocities.right * differentialDrive.wheelRadius).meters.velocity,
            wheelVoltages.right.volts
        )
    }
}