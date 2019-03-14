package org.ghrobotics.lib.simulation

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.radian
import org.ghrobotics.lib.subsystems.drive.DifferentialTrackerDriveBase

class SimDifferentialDrive(
    override val differentialDrive: DifferentialDrive,
    override val leftMotor: SimFalconMotor<Length>,
    override val rightMotor: SimFalconMotor<Length>,
    override val trajectoryTracker: TrajectoryTracker,
    private val angularFactor: Double = 1.0
) : DifferentialTrackerDriveBase {

    override var robotPosition = Pose2d()

    fun update(deltaTime: Time) {
        val wheelState = DifferentialDrive.WheelState(
            leftMotor.velocity * deltaTime.value / differentialDrive.wheelRadius,
            rightMotor.velocity * deltaTime.value / differentialDrive.wheelRadius
        )

        val forwardKinematics = differentialDrive.solveForwardKinematics(wheelState)

        robotPosition += Twist2d(
            forwardKinematics.linear,
            0.0,
            (forwardKinematics.angular * angularFactor).radian
        ).asPose
    }

}