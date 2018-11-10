package org.ghrobotics.lib.subsystems.drive.characterization

import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.subsystems.drive.TankDriveSubsystem
import org.ghrobotics.lib.utils.DeltaTime

class StepVoltageCharacterizationTest(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
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
            (driveSubsystem.leftMaster.motorOutputVoltage + driveSubsystem.rightMaster.motorOutputVoltage) / 2.0

        val avgSpd =
            (driveSubsystem.leftMaster.sensorVelocity + driveSubsystem.rightMaster.sensorVelocity).value /
                2.0 / wheelRadius.value

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