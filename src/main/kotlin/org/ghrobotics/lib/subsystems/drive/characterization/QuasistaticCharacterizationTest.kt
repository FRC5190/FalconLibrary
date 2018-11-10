package org.ghrobotics.lib.subsystems.drive.characterization

import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.subsystems.drive.TankDriveSubsystem
import org.ghrobotics.lib.utils.DeltaTime

class QuasistaticCharacterizationTest(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val turnInPlace: Boolean
) : FalconCommand(driveSubsystem) {

    private val data = ArrayList<CharacterizationData>()
    private val dtController = DeltaTime()

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
            (driveSubsystem.leftMaster.motorOutputVoltage + driveSubsystem.rightMaster.motorOutputVoltage) / 2.0

        val avgSpd =
            (driveSubsystem.leftMaster.sensorVelocity + driveSubsystem.rightMaster.sensorVelocity).value /
                2.0 / wheelRadius.value

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