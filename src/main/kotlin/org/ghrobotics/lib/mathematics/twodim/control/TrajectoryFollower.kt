/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.DefaultTrajectoryGenerator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedIterator
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.derivedunits.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derivedunits.Volt
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Follows a smooth trajectory.
 *
 * @param drive Instance of the differential drive that represents the dynamics of the drivetrain.
 */
abstract class TrajectoryFollower(private val drive: DifferentialDrive) {

    // Calculate chassis velocity based on the specific algorithm.
    abstract fun calculateChassisVelocity(robotPose: Pose2d): DifferentialDrive.ChassisState

    // Store the previous velocity for acceleration calculations.
    private var previousVelocity = DifferentialDrive.ChassisState(0.0, 0.0)

    // Trajectory iterator
    protected lateinit var iterator: TimedIterator<Pose2dWithCurvature>
        private set

    // Current reference point, pose, and variable to store if the iterator has finished.
    val referencePoint get() = iterator.currentState
    val referencePose get() = referencePoint.state.state.pose
    val isFinished get() = iterator.isDone

    // Loops
    private var deltaTimeController = DeltaTime()

    init {
        resetTrajectory(DefaultTrajectoryGenerator.baseline)
    }

    /**
     * Reset the iterator with a new trajectory.
     *
     * @param trajectory The new trajectory to renew the iterator.
     */
    fun resetTrajectory(trajectory: TimedTrajectory<Pose2dWithCurvature>) {
        iterator = trajectory.iterator()
        previousVelocity = DifferentialDrive.ChassisState(0.0, 0.0)
        deltaTimeController.reset()
    }

    /**
     * Return output from kinematic calculations only. Robot dynamics are not taken into account.
     *
     * @param robot Current robot pose
     * @param currentTime Current time
     */
    fun getOutputFromKinematics(robot: Pose2d, currentTime: Time = System.nanoTime().nanosecond): Output {
        val dt = deltaTimeController.updateTime(currentTime)

        val chassisVelocity = calculateChassisVelocity(robot)

        val wheelVelocities = drive.solveInverseKinematics(chassisVelocity)
        val feedForwardVoltages = drive.getVoltagesFromkV(wheelVelocities)

        iterator.advance(dt)
        return outputFromWheelStates(drive, wheelVelocities, feedForwardVoltages)
    }

    /**
     *  Return output from dynamic calculations. Robot dynamics are taken into account.
     *
     *  @param robot Current robot pose
     *  @param currentTime Current time
     */
    fun getOutputFromDynamics(robot: Pose2d, currentTime: Time = System.nanoTime().nanosecond): Output {
        val dt = deltaTimeController.updateTime(currentTime)

        val chassisVelocity = calculateChassisVelocity(robot)
        val chassisAcceleration = if (dt == 0.0.second) {
            DifferentialDrive.ChassisState(0.0, 0.0)
        } else {
            (chassisVelocity - previousVelocity) / dt.second
        }

        val dynamics = drive.solveInverseDynamics(chassisVelocity, chassisAcceleration)

        previousVelocity = dynamics.chassisVelocity
        iterator.advance(dt)
        return outputFromWheelStates(drive, dynamics.wheelVelocity, dynamics.voltage)
    }

    data class Output(
        val leftSetPoint: LinearVelocity,
        val rightSetPoint: LinearVelocity,
        val leftVoltage: Volt,
        val rightVoltage: Volt
    )

    companion object {
        private fun outputFromWheelStates(
            drive: DifferentialDrive,
            setPoint: DifferentialDrive.WheelState,
            voltages: DifferentialDrive.WheelState
        ) = Output(
            leftSetPoint = (setPoint.left * drive.wheelRadius).meter.velocity,
            rightSetPoint = (setPoint.right * drive.wheelRadius).meter.velocity,
            leftVoltage = voltages.left.volt,
            rightVoltage = voltages.right.volt
        )
    }
}