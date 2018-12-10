package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import edu.wpi.first.wpilibj.drive.DifferentialDrive
import kotlinx.coroutines.ObsoleteCoroutinesApi
import org.ghrobotics.lib.commands.ConditionCommand
import org.ghrobotics.lib.commands.FalconCommandGroup
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.sequential
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryFollower
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Rectangle2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.mirror
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.subsystems.drive.characterization.QuasistaticCharacterizationCommand
import org.ghrobotics.lib.subsystems.drive.characterization.StepVoltageCharacterizationCommand
import org.ghrobotics.lib.subsystems.drive.localization.Localization
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.map
import org.ghrobotics.lib.wrappers.LinearFalconSRX
import kotlin.math.absoluteValue
import kotlin.math.max
import kotlin.math.withSign

/**
 * Represents a standard tank drive subsystem
 */
abstract class TankDriveSubsystem : FalconSubsystem("Drive Subsystem") {

    abstract val leftMaster: LinearFalconSRX
    abstract val rightMaster: LinearFalconSRX

    abstract val localization: Localization

    abstract val trajectoryFollower: TrajectoryFollower

    private var quickStopAccumulator = 0.0

    /**
     * Initialize odometry
     */
    @ObsoleteCoroutinesApi
    override fun lateInit() {
        localization.start()
    }

    /**
     * Zero the outputs of the drivetrain.
     */
    override fun zeroOutputs() {
        tankDrive(0.0, 0.0)
    }

    // COMMON DRIVE TYPES

    /**
     * Arcade drive control
     */
    fun arcadeDrive(
        linearPercent: Double,
        rotationPercent: Double
    ) {
        val maxInput = max(linearPercent.absoluteValue, rotationPercent.absoluteValue)
            .withSign(linearPercent)

        val leftMotorOutput: Double
        val rightMotorOutput: Double

        if (linearPercent >= 0.0) {
            // First quadrant, else second quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            } else {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            } else {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            }
        }

        tankDrive(leftMotorOutput, rightMotorOutput)
    }

    /**
     * Curvature or cheezy drive control
     */
    fun curvatureDrive(
        linearPercent: Double,
        curvaturePercent: Double,
        isQuickTurn: Boolean
    ) {
        val angularPower: Double
        val overPower: Boolean

        if (isQuickTurn) {
            if (linearPercent.absoluteValue < kQuickStopThreshold) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator +
                    kQuickStopAlpha * curvaturePercent.coerceIn(-1.0, 1.0) * 2.0
            }
            overPower = true
            angularPower = curvaturePercent
        } else {
            overPower = false
            angularPower = linearPercent.absoluteValue * curvaturePercent - quickStopAccumulator

            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1.0
                quickStopAccumulator < -1 -> quickStopAccumulator += 1.0
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = linearPercent + angularPower
        var rightMotorOutput = linearPercent - angularPower

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (overPower) {
            when {
                leftMotorOutput > 1.0 -> {
                    rightMotorOutput -= leftMotorOutput - 1.0
                    leftMotorOutput = 1.0
                }
                rightMotorOutput > 1.0 -> {
                    leftMotorOutput -= rightMotorOutput - 1.0
                    rightMotorOutput = 1.0
                }
                leftMotorOutput < -1.0 -> {
                    rightMotorOutput -= leftMotorOutput + 1.0
                    leftMotorOutput = -1.0
                }
                rightMotorOutput < -1.0 -> {
                    leftMotorOutput -= rightMotorOutput + 1.0
                    rightMotorOutput = -1.0
                }
            }
        }

        // Normalize the wheel speeds
        val maxMagnitude = max(leftMotorOutput.absoluteValue, rightMotorOutput.absoluteValue)
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        tankDrive(leftMotorOutput, rightMotorOutput)
    }


    /**
     * Tank drive control
     */
    fun tankDrive(
        leftPercent: Double,
        rightPercent: Double
    ) {
        leftMaster.set(ControlMode.PercentOutput, leftPercent.coerceIn(-1.0, 1.0))
        rightMaster.set(ControlMode.PercentOutput, rightPercent.coerceIn(-1.0, 1.0))
    }


    // PRE GENERATED TRAJECTORY METHODS

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     */
    fun followTrajectory(
        trajectory: TimedTrajectory<Pose2dWithCurvature>
    ) = FollowTrajectoryCommand(this, trajectory)

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     * @param pathMirrored Whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: TimedTrajectory<Pose2dWithCurvature>,
        pathMirrored: Boolean = false
    ) = followTrajectory(trajectory.let {
        if (pathMirrored) it.mirror() else it
    })

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory Source with the trajectory to follow
     * @param pathMirrored Whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Source<TimedTrajectory<Pose2dWithCurvature>>,
        pathMirrored: Boolean = false
    ) = FollowTrajectoryCommand(this, trajectory.map {
        if (pathMirrored) it.mirror() else it
    })

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory The trajectory to follow
     * @param pathMirrored Source with whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: TimedTrajectory<Pose2dWithCurvature>,
        pathMirrored: BooleanSource
    ) = followTrajectory(pathMirrored.map(trajectory.mirror(), trajectory))

    /**
     * Returns the follow trajectory command
     *
     * @param trajectory Source with yhe trajectory to follow
     * @param pathMirrored Source with whether to mirror the path or not
     */
    fun followTrajectory(
        trajectory: Source<TimedTrajectory<Pose2dWithCurvature>>,
        pathMirrored: BooleanSource
    ) = followTrajectory(pathMirrored.map(trajectory.map { it.mirror() }, trajectory))


    // REGIONAL CONDITIONAL COMMAND METHODS

    /**
     * Returns a condition command that checks if the robot is in a specified region
     *
     * @param region The region to check if the robot is in.
     */
    fun withinRegion(region: Rectangle2d) =
        withinRegion(Source(region))


    /**
     * Returns a condition command that checks if the robot is in a specified region
     *
     * @param region Source with the region to check if the robot is in.
     */
    fun withinRegion(region: Source<Rectangle2d>) =
        ConditionCommand { region().contains(localization().translation) }


    // DRIVE CHARACTERIZATION

    /**
     * Characterizes the drivetrain and prints out CSV data to the console.
     * All linear and angular Kv and Ka parameters can be found by calling this method.
     */
    open fun characterizeDrive(wheelRadius: Length, effectiveWheelBaseRadius: Length): FalconCommandGroup =
        sequential {
            +QuasistaticCharacterizationCommand(this@TankDriveSubsystem, wheelRadius, effectiveWheelBaseRadius, false)
            +ConditionCommand {
                leftMaster.sensorVelocity.value.absoluteValue < kEpsilon &&
                    rightMaster.sensorVelocity.value.absoluteValue < kEpsilon
            }
            +StepVoltageCharacterizationCommand(this@TankDriveSubsystem, wheelRadius, effectiveWheelBaseRadius, false)
            +ConditionCommand {
                leftMaster.sensorVelocity.value.absoluteValue < kEpsilon &&
                    rightMaster.sensorVelocity.value.absoluteValue < kEpsilon
            }
            +QuasistaticCharacterizationCommand(this@TankDriveSubsystem, wheelRadius, effectiveWheelBaseRadius, true)
            +ConditionCommand {
                leftMaster.sensorVelocity.value.absoluteValue < kEpsilon &&
                    rightMaster.sensorVelocity.value.absoluteValue < kEpsilon
            }
            +StepVoltageCharacterizationCommand(this@TankDriveSubsystem, wheelRadius, effectiveWheelBaseRadius, true)
        }

    companion object {
        const val kQuickStopThreshold = DifferentialDrive.kDefaultQuickStopThreshold
        const val kQuickStopAlpha = DifferentialDrive.kDefaultQuickStopAlpha
    }
}