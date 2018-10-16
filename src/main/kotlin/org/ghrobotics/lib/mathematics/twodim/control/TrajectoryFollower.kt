/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.Volt
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.DeltaTime

abstract class TrajectoryFollower(val trajectory: TimedTrajectory<Pose2dWithCurvature>,
                                  private val drive: DifferentialDrive) {

    abstract fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState

    private var previousVelocity = DifferentialDrive.ChassisState(0.0, 0.0)

    protected val iterator = trajectory.iterator()

    val referencePoint
        get() = iterator.currentState

    val referencePose
        get() = referencePoint.state.state.pose

    val isFinished
        get() = iterator.isDone


    // Loops
    private var deltaTimeController = DeltaTime()


    // Return output from kinematic calculations only. Robot dynamics are not taken into account.
    fun getOutputFromKinematics(robot: Pose2d, currentTime: Time = System.nanoTime().nanosecond): Output {
        val dt = deltaTimeController.updateTime(currentTime)

        val chassisVelocity = calculateChassisVelocity(robot)

        val wheelVelocities = drive.solveInverseKinematics(chassisVelocity)
        val feedforwardVoltages = drive.getVoltagesFromkV(wheelVelocities)

        iterator.advance(dt)
        return outputFromWheelStates(drive, wheelVelocities, feedforwardVoltages)
    }

    // Return output from dynamic calculations. Robot dynamics are taken into account.
    fun getOutputFromDynamics(robot: Pose2d, currentTime: Time = System.nanoTime().nanosecond): Output {
        val dt = deltaTimeController.updateTime(currentTime)

        val chassisVelocity = calculateChassisVelocity(robot)
        val chassisAcceleration = if (dt == 0.0.second) {
            DifferentialDrive.ChassisState(0.0, 0.0)
        } else {
            (chassisVelocity - previousVelocity) / dt.second.asDouble
        }

        val dynamics = drive.solveInverseDynamics(chassisVelocity, chassisAcceleration)

        previousVelocity = dynamics.chassisVelocity
        iterator.advance(dt)
        return outputFromWheelStates(drive, dynamics.wheelVelocity, dynamics.voltage)
    }

    data class Output(val lSetpoint: Velocity, val rSetpoint: Velocity, val lfVoltage: Volt, val rfVoltage: Volt)

    companion object {
        private fun outputFromWheelStates(drive: DifferentialDrive,
                                          setpoint: DifferentialDrive.WheelState,
                                          voltages: DifferentialDrive.WheelState) = Output(
                lSetpoint = (setpoint.left * drive.wheelRadius).meter.velocity,
                rSetpoint = (setpoint.right * drive.wheelRadius).meter.velocity,
                lfVoltage = voltages.left.volt,
                rfVoltage = voltages.right.volt
        )
    }
}