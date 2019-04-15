package org.ghrobotics.lib.subsystems.drive.characterization

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.subsystems.drive.TankDriveSubsystem
import org.ghrobotics.lib.utils.DeltaTime

/**
 * Runs a quasistatic test by ramping voltage slowly to measure Kv.
 *
 * @param driveSubsystem The drive subsystem
 * @param wheelRadius Wheel radius
 * @param turnInPlace Whether the test should move forward for linear Kv or turn in place for angular Kv
 */
class QuasistaticCharacterizationCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val effectiveWheelBaseRadius: Length,
    private val turnInPlace: Boolean
) : FalconCommand(driveSubsystem) {

    // Holds the characterization data
    private val data = ArrayList<CharacterizationData>()

    // Controller that takes care of the delta time
    private val dtController = DeltaTime()

    // Some variables to keep track
    private var startTime = 0.millisecond
    private var commandedVoltage = 0.0 // V

    init {
        finishCondition += { commandedVoltage >= kMaxVoltage }
        executeFrequency = 10 // Hz
    }

    override suspend fun initialize() {
        startTime = System.currentTimeMillis().millisecond
        dtController.reset()
    }

    override suspend fun execute() {
        commandedVoltage = kRampRate * (System.currentTimeMillis().millisecond - startTime).second
        val dt = dtController.updateTime(System.currentTimeMillis().millisecond)

        driveSubsystem.tankDrive(commandedVoltage / 12.0, commandedVoltage / 12.0 * if (turnInPlace) -1 else 1)

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
        println("VELOCITY DATA")
        data.forEach { System.out.println(it.toCSV()) }
    }

    companion object {
        private const val kRampRate = 0.15 // V per sec
        private const val kMaxVoltage = 4.0 // V
    }

}