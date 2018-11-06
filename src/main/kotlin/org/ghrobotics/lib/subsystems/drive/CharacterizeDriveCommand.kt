@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.ghrobotics.lib.subsystems.drive

import com.ctre.phoenix.motorcontrol.ControlMode
import org.apache.commons.math3.stat.regression.SimpleRegression
import org.ghrobotics.lib.commands.FalconCommand
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.utils.DeltaTime
import kotlin.math.absoluteValue

class CharacterizeVelocityCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val turnInPlace: Boolean,
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
            (driveSubsystem.leftMaster.sensorVelocity.value.absoluteValue +
                driveSubsystem.rightMaster.sensorVelocity.value.absoluteValue) /
                2.0 / wheelRadius.value

        if (speed > kEpsilon) {
            dataReference.add(CharacterizeVelocityCommand.Data(commandedVoltage, speed))
        }

        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, commandedVoltage / 12.0)
        driveSubsystem.rightMaster.set(
            ControlMode.PercentOutput,
            if (turnInPlace) -1.0 else 1.0 * commandedVoltage / 12.0
        )
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }
}

class CharacterizeAccelerationCommand(
    private val driveSubsystem: TankDriveSubsystem,
    private val wheelRadius: Length,
    private val turnInPlace: Boolean,
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

    override suspend fun initialize() {
        deltaTime.reset()
    }

    override suspend fun execute() {
        val dt = deltaTime.updateTime(System.nanoTime().nanosecond)
        val speed =
            (driveSubsystem.leftMaster.sensorVelocity.value.absoluteValue +
                driveSubsystem.rightMaster.sensorVelocity.value.absoluteValue) /
                2.0 / wheelRadius.value

        if (dt == 0.second) {
            previousSpeed = speed
            return
        }

        val acceleration = (speed - previousSpeed) / dt.second
        previousSpeed = speed

        dataReference.add(CharacterizeAccelerationCommand.Data(voltage, speed, acceleration))

        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, voltage / 12.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, if (turnInPlace) -1.0 else 1.0 * voltage / 12.0)
    }

    override suspend fun dispose() {
        driveSubsystem.leftMaster.set(ControlMode.PercentOutput, 0.0)
        driveSubsystem.rightMaster.set(ControlMode.PercentOutput, 0.0)
    }
}

object CharacterizationCalculator {

    fun computeKv(velocityData: ArrayList<CharacterizeVelocityCommand.Data>): Double {
        val regression = SimpleRegression()
        velocityData.forEach { regression.addData(it.radPerSec, it.voltage) }
        return regression.slope
    }

    fun computeKs(velocityData: ArrayList<CharacterizeVelocityCommand.Data>): Double {
        val regression = SimpleRegression()
        velocityData.forEach { regression.addData(it.radPerSec, it.voltage) }
        return regression.intercept
    }

    fun computeKa(
        accelerationData: ArrayList<CharacterizeAccelerationCommand.Data>,
        velocityData: ArrayList<CharacterizeVelocityCommand.Data>
    ): Double {

        val kV = computeKv(velocityData)
        val kS = computeKs(velocityData)
        val regression = SimpleRegression()

        accelerationData.forEach {
            regression.addData(
                it.radPerSecPerSec,
                it.voltage - kV * it.radPerSec - kS
            )
        }

        return regression.slope
    }


    fun getDifferentialDriveConstants(
        wheelRadius: Length,
        effectiveWheelBaseRadius: Length,
        robotMass: Mass,
        linearVelocityData: ArrayList<CharacterizeVelocityCommand.Data>,
        angularVelocityData: ArrayList<CharacterizeVelocityCommand.Data>,
        linearAccelerationData: ArrayList<CharacterizeAccelerationCommand.Data>,
        angularAccelerationData: ArrayList<CharacterizeAccelerationCommand.Data>
    ): CharacterizationData {

        // Compute linear Kv term. In terms of volts per rad/s
        val linearKv = computeKv(linearVelocityData)

        // Compute linear and angular Ka terms. In terms of volts per rad/s/s
        val linearKa = computeKa(linearAccelerationData, linearVelocityData)
        val angularKa = computeKa(angularAccelerationData, angularVelocityData)

        // Get a value for linear acceleration by finding the maximum acceleration
        val linearAcceleration = linearAccelerationData.maxBy { it.radPerSecPerSec }!!.radPerSecPerSec * wheelRadius.value

        // Get a value for angular acceleration by finding the maximum acceleration
        val angularAcceleration = angularAccelerationData.maxBy { it.radPerSecPerSec }!!.radPerSecPerSec

        // Calculate the torque required for the acceleration in the linear case.
        // This relationship is linear.
        // torque / wheel radius = mass * linear acceleration.
        val torque = robotMass.value * wheelRadius.value * wheelRadius.value * linearAcceleration

        // Assume the same amount of torque is available in the angular case.
        // Use this assumption to calculate the moment of inertia.
        // Torque / wheel radius * effective track width radius = angular acceleration * robot moi
        val momentOfInertia = torque / wheelRadius.value * effectiveWheelBaseRadius.value / angularAcceleration

        return CharacterizationData(
            linearKv, computeKs(linearVelocityData),
            linearKa, angularKa, momentOfInertia
        )
    }

    data class CharacterizationData(
        val linearKv: Double, val linearKs: Double, val linearKa: Double,
        val angularKa: Double, val momentOfInertia: Double
    )

}