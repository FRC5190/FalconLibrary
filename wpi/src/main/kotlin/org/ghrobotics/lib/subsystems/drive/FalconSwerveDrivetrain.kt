package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.controller.SimpleMotorFeedforward
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveDriveOdometry
import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.debug.FalconDashboard
import org.ghrobotics.lib.localization.TimePoseInterpolatableBuffer
import org.ghrobotics.lib.mathematics.twodim.geometry.x_u
import org.ghrobotics.lib.mathematics.twodim.geometry.y_u
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.inFeet
import org.ghrobotics.lib.mathematics.units.meters
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.seconds
import org.ghrobotics.lib.subsystems.SensorlessCompatibleSubsystem
import org.ghrobotics.lib.utils.Source

abstract class FalconSwerveDrivetrain : TrajectoryTrackerSwerveDriveBase(), SensorlessCompatibleSubsystem {
    /**
     * The current inputs and outputs
     */
    protected val periodicIO = PeriodicIO()

    /**
     * Helper for different drive styles.
     */
    protected val driveHelper = FalconDriveHelper()

    /**
     * The odometry object that is used to calculate the robot's position
     * on the field.
     */
    abstract val odometry: SwerveDriveOdometry

    /**
     * Buffer for storing the pose over a span of time. This is useful for
     * Vision and latency compensation.
     */
    protected open val poseBuffer = TimePoseInterpolatableBuffer()

    /**
     * The left front motor
     */
    protected abstract val modules: Array<FalconSwerveModule>

    /**
     * The characterization for the left front swerve module.
     */
    abstract val leftFrontCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the right front swerve module.
     */
    abstract val rightFrontCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the left back swerve module.
     */
    abstract val leftBackCharacterization: SimpleMotorFeedforward

    /**
     * The characterization for the right back swerve module.
     */
    abstract val rightBackCharacterization: SimpleMotorFeedforward

    /**
     * The rotation source / gyro
     */
    abstract val gyro: Source<Rotation2d>

    /**
     * Get the robot's position on the field.
     */
    override var robotPosition: Pose2d = Pose2d()


    

    override fun periodic() {
        periodicIO.leftFrontVoltage = modules[0].voltageOutput
        periodicIO.rightFrontVoltage = modules[1].voltageOutput
        periodicIO.leftBackVoltage = modules[2].voltageOutput
        periodicIO.rightBackVoltage = modules[3].voltageOutput


        periodicIO.leftFrontCurrent = modules[0].drawnCurrent
        periodicIO.rightFrontCurrent = modules[1].drawnCurrent
        periodicIO.leftBackCurrent = modules[2].drawnCurrent
        periodicIO.rightBackCurrent = modules[3].drawnCurrent

        periodicIO.leftFrontPosition = modules[0].drivePosition
        periodicIO.rightFrontPosition = modules[1].drivePosition
        periodicIO.leftBackPosition = modules[2].drivePosition
        periodicIO.rightBackPosition = modules[3].drivePosition

        periodicIO.leftFrontVelocity = modules[0].driveVelocity
        periodicIO.rightFrontVelocity = modules[1].driveVelocity
        periodicIO.leftBackVelocity = modules[2].driveVelocity
        periodicIO.rightBackVelocity = modules[3].driveVelocity

        periodicIO.gyro = gyro()

        val leftFrontFeedforward = periodicIO.leftFrontFeedforward
        val rightFrontFeedforward = periodicIO.rightFrontFeedforward
        val leftBackFeedforward = periodicIO.leftBackFeedforward
        val rightBackFeedforward = periodicIO.rightBackFeedforward

        robotPosition = odometry.update(
            periodicIO.gyro, *kinematics.toSwerveModuleStates(kinematics.toChassisSpeeds())
        )
        poseBuffer[Timer.getFPGATimestamp().seconds] = robotPosition

        when (val desiredOutput = periodicIO.desiredOutput) {
            is Output.Nothing -> {
                modules[0].setNeutral()
                modules[1].setNeutral()
                modules[2].setNeutral()
                modules[3].setNeutral()
            }
            is Output.Percent -> {
                modules[0].setPercent(desiredOutput.leftFront, leftFrontFeedforward)
                modules[1].setPercent(desiredOutput.rightFront, rightFrontFeedforward)
                modules[2].setPercent(desiredOutput.leftBack, leftBackFeedforward)
                modules[3].setPercent(desiredOutput.rightBack, rightBackFeedforward)
            }
            is Output.Velocity -> {
                modules[0].setVelocity(desiredOutput.leftFront, leftFrontFeedforward)
                modules[1].setVelocity(desiredOutput.rightFront, rightFrontFeedforward)
                modules[2].setVelocity(desiredOutput.leftBack, leftFrontFeedforward)
                modules[3].setVelocity(desiredOutput.rightBack, rightFrontFeedforward)
            }
        }

        FalconDashboard.robotHeading = robotPosition.rotation.radians
        FalconDashboard.robotX = robotPosition.translation.x_u.inFeet()
        FalconDashboard.robotY = robotPosition.translation.y_u.inFeet()
    }

    /**
     * Represents periodic data
     */
    protected class PeriodicIO {
        var leftFrontVoltage: SIUnit<Volt> = 0.volts
        var rightFrontVoltage: SIUnit<Volt> = 0.volts
        var leftBackVoltage: SIUnit<Volt> = 0.volts
        var rightBackVoltage: SIUnit<Volt> = 0.volts

        var leftFrontCurrent: SIUnit<Ampere> = 0.amps
        var rightFrontCurrent: SIUnit<Ampere> = 0.amps
        var leftBackCurrent: SIUnit<Ampere> = 0.amps
        var rightBackCurrent: SIUnit<Ampere> = 0.amps

        var leftFrontPosition: SIUnit<Meter> = 0.meters
        var rightFrontPosition: SIUnit<Meter> = 0.meters
        var leftBackPosition: SIUnit<Meter> = 0.meters
        var rightBackPosition: SIUnit<Meter> = 0.meters

        var leftFrontVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var rightFrontVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var leftBackVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds
        var rightBackVelocity: SIUnit<LinearVelocity> = 0.meters / 1.seconds


        var gyro: Rotation2d = Rotation2d()

        var desiredOutput: Output = Output.Nothing

        var leftFrontFeedforward: SIUnit<Volt> = 0.volts
        var rightFrontFeedforward: SIUnit<Volt> = 0.volts
        var leftBackFeedforward: SIUnit<Volt> = 0.volts
        var rightBackFeedforward: SIUnit<Volt> = 0.volts
    }

    /**
     * Represents the typical outputs for the drivetrain.
     */
    protected sealed class Output {
        // No outputs
        object Nothing : Output()

        // Percent Output
        class Percent(val leftFront: Double, val rightFront: Double, val leftBack: Double, val rightBack: Double) : Output()

        // Velocity Output
        class Velocity(
            val leftFront: SIUnit<LinearVelocity>,
            val rightFront: SIUnit<LinearVelocity>,
            val leftBack: SIUnit<LinearVelocity>,
            val rightBack: SIUnit<LinearVelocity>,
        ) : Output()
    }

}