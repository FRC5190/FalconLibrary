@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.DeltaTime


class CharacterizeVelocityCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val dataReference: ArrayList<CharacterizeVelocityCommand.Data>,
    maxVoltage: Double = 4.0
) : FalconCommand(driveSubsystem) {

    data class Data(val voltage: Double, val radPerSec: Double)

    var commandedVoltage = 0.0 // Volts

    init {
        finishCondition += { commandedVoltage >= maxVoltage }
        executeFrequency = 1 // Hertz
    }

    override suspend fun execute() {
        val speed =
            (driveSubsystem.leftMaster.sensorVelocity.value + driveSubsystem.rightMaster.sensorVelocity.value) /
                2.0 / wheelRadius.value

        if (speed > kEpsilon) {
            dataReference.add(CharacterizeVelocityCommand.Data(commandedVoltage, speed))
        }

        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, commandedVoltage / 12.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, commandedVoltage / 12.0)
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }
}

class CharacterizeAccelerationCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val dataReference: ArrayList<CharacterizeAccelerationCommand.Data>,
    private val voltage: Double = 10.0,
    timeout: Time = 2.second
) : FalconCommand(driveSubsystem) {

    data class Data(val voltage: Double, val radPerSec: Double, val radPerSecPerSec: Double)

    val deltaTime = DeltaTime()
    var previousSpeed = 0.0

    init {
        withTimeout(timeout)
    }

    override suspend fun execute() {
        val dt = deltaTime.updateTime(System.nanoTime().nanosecond)
        val speed =
            (driveSubsystem.leftMaster.sensorVelocity.value + driveSubsystem.rightMaster.sensorVelocity.value) /
                2.0 / wheelRadius.value

        if (dt == 0.second) {
            previousSpeed = speed
            return
        }

        val acceleration = (speed - previousSpeed) / dt.second
        previousSpeed = speed

        dataReference.add(CharacterizeAccelerationCommand.Data(voltage, speed, acceleration))

        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, voltage / 12.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, voltage / 12.0)
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }
}

class CharacterizationCalculator(
    private val velocityData: ArrayList<CharacterizeVelocityCommand.Data>,
    private val accelerationData: ArrayList<CharacterizeAccelerationCommand.Data>
) {

    var kV = 0.0 // Volts per rad/s
    var kA = 0.0 // Volts per rad/s/s
    var kS = 0.0 // Volts

    fun getConstants() {
        // First, compute kV and kS terms
        val velocityRegression = SimpleRegression()
        velocityData.forEach { velocityRegression.addData(it.radPerSec, it.voltage) }

        kV = velocityRegression.slope
        kS = velocityRegression.intercept

        // Now compute the kA term
        val accelerationRegression = SimpleRegression()

        accelerationData.forEach {
            accelerationRegression.addData(
                it.radPerSecPerSec,
                it.voltage - kV * it.radPerSec - kS // Use the extra voltage required to accelerate.
                // We don't want to factor in the voltage from keeping the robot at a velocity.
            )
        }

        kA = accelerationRegression.slope

        println("kV: $kV, kA: $kA, kS: $kS")
        println("Velocity R^2: ${velocityRegression.rSquare}, Acceleration R^2: ${accelerationRegression.rSquare}")

    }
}