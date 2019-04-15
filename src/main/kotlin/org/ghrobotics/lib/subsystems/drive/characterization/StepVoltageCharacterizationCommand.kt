package org.ghrobotics.lib.subsystems.drive.characterization

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.subsystems.drive.TankDriveSubsystem
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Runs a step voltage test by setting a constant voltage to measure Ka
 *
 * @param driveSubsystem The drive subsystem
 * @param wheelRadius Wheel radius
 * @param turnInPlace Whether the test should move forward for linear Ka or turn in place for angular Ka
 */
class StepVoltageCharacterizationCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val effectiveWheelBaseRadius: Length,
    private val turnInPlace: Boolean
) : FalconCommand(driveSubsystem) {

    private val data = ArrayList<CharacterizationData>()
    private val dtController = DeltaTime()

    init {
        withTimeout(kTimeout)
        executeFrequency = 10 // Hz
    }

    override suspend fun initialize() {
        dtController.reset()
    }

    override suspend fun execute() {
        val dt = dtController.updateTime(System.currentTimeMillis().millisecond)

        driveSubsystem.tankDrive(kStepVoltage / 12.0, kStepVoltage / 12.0 * if (turnInPlace) -1 else 1)

        val avgCompensatedVoltage =
            (driveSubsystem.leftMotor.voltageOutput + driveSubsystem.rightMotor.voltageOutput) / 2.0

        val wheelMotion = DifferentialDrive.WheelState(
            driveSubsystem.leftMotor.encoder.velocity,
            driveSubsystem.rightMotor.encoder.velocity
        )

        // Return robot speed in meters per second if linear, radians per second if angular
        val avgSpd: Double = if (turnInPlace) {
            (wheelMotion.right - wheelMotion.left) / (2.0 * effectiveWheelBaseRadius.value)
        } else {
            (wheelMotion.right + wheelMotion.left) / 2.0
        }

        data.add(CharacterizationData(avgCompensatedVoltage, avgSpd, dt.second))
    }

    override suspend fun dispose() {
        println("ACCELERATION DATA")
        data.forEach { System.out.println(it.toCSV()) }
    }

    companion object {
        private val kTimeout = 2.second
        private const val kStepVoltage = 6.0
    }

}