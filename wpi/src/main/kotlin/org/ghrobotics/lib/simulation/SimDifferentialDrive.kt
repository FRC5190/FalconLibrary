/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.simulation

import com.team254.lib.physics.DifferentialDrive
import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Second
import org.ghrobotics.lib.mathematics.units.operations.times
import org.ghrobotics.lib.subsystems.drive.DifferentialTrackerDriveBase

class SimDifferentialDrive(
    override val differentialDrive: DifferentialDrive,
    override val leftMotor: SimFalconMotor<Meter>,
    override val rightMotor: SimFalconMotor<Meter>,
    override val trajectoryTracker: TrajectoryTracker,
    private val angularFactor: Double = 1.0
) : DifferentialTrackerDriveBase {

    override var robotPosition = Pose2d()

    fun update(deltaTime: SIUnit<Second>) {
        val wheelState = DifferentialDrive.WheelState(
            (leftMotor.velocity * deltaTime / differentialDrive.wheelRadius).value,
            (rightMotor.velocity * deltaTime / differentialDrive.wheelRadius).value
        )

        val forwardKinematics = differentialDrive.solveForwardKinematics(wheelState)

        robotPosition = robotPosition.exp(
            Twist2d(
                forwardKinematics.linear,
                0.0,
                (forwardKinematics.angular * angularFactor)
            )
        )
    }

}