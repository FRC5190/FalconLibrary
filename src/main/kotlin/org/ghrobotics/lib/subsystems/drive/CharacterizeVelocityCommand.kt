package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.map

class CharacterizeCoupledVelocityCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length
) : FalconCommand(driveSubsystem) {

    private var commandedVoltage = 0.0
    private var vIntercept: Double? = null
    private val voltageSpeedData = HashMap<Double, Double>()

    private val avgSpd
        get() = (driveSubsystem.leftMaster.sensorVelocity + driveSubsystem.rightMaster.sensorVelocity) / 2.0
    private val avgRadPerSec
        get() = avgSpd.value / wheelRadius.value

    init {
        executeFrequency = 1 // Hz
        finishCondition += { commandedVoltage > 12.0 }
    }

    override suspend fun execute() {
        commandedVoltage += 0.25
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, commandedVoltage / 12.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, commandedVoltage / 12.0)

        if (avgRadPerSec > kEpsilon) {
            val averageVoltage =
                (driveSubsystem.leftMaster.motorOutputVoltage + driveSubsystem.rightMaster.motorOutputVoltage) / 2.0

            if (vIntercept == null) {
                vIntercept = averageVoltage
            }

            voltageSpeedData[averageVoltage] = avgRadPerSec
        }
    }

    override suspend fun dispose() {
        val regression = SimpleRegression()
        voltageSpeedData.forEach { voltage, speed -> regression.addData(voltage, speed) }

        System.out.printf(
            "kV: %3.3f Volts per radians per second, kS: %3.3f Volts. Linearity: %3.3f %n",
            1 / regression.slope, vIntercept, regression.rSquare
        )
    }
}

class CharacterizeDecoupledVelocityCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val characterizeLeft: BooleanSource
) : FalconCommand(driveSubsystem) {

    private var commandedVoltage = 0.0
    private var vIntercept: Double? = null
    private val voltageSpeedData = HashMap<Double, Double>()

    private val talon = characterizeLeft.map(driveSubsystem.leftMaster, driveSubsystem.rightMaster)()

    private val avgSpd
        get() = talon.sensorVelocity

    private val avgRadPerSec
        get() = avgSpd.value / wheelRadius.value

    init {
        executeFrequency = 1 // Hz
        finishCondition += { commandedVoltage > 12.0 }
    }

    override suspend fun execute() {
        commandedVoltage += 0.25
        talon.set(ControlMode.PercentOutput, commandedVoltage / 12.0)

        if (avgRadPerSec > kEpsilon) {
            val averageVoltage = talon.motorOutputVoltage

            if (vIntercept == null) {
                vIntercept = averageVoltage
            }

            voltageSpeedData[averageVoltage] = avgRadPerSec
        }
    }

    override suspend fun dispose() {
        val regression = SimpleRegression()
        voltageSpeedData.forEach { voltage, speed -> regression.addData(voltage, speed) }

        val side = characterizeLeft.map("Left", "Right")()

        System.out.printf(
            "[$side] kV: %3.3f Volts per radians per second, kS: %3.3f Volts. Linearity: %3.3f %n",
            1 / regression.slope, vIntercept, regression.rSquare
        )
    }
}