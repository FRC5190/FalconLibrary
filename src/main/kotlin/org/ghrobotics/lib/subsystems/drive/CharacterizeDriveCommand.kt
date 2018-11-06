@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package org.ghrobotics.lib.subsystems.drive

/* ktlint-disable no-wildcard-imports */
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
        println("computeKv() R^2: ${regression.rSquare}")
        return regression.slope
    }

    fun computeKs(velocityData: ArrayList<CharacterizeVelocityCommand.Data>): Double {
        val regression = SimpleRegression()
        velocityData.forEach { regression.addData(it.radPerSec, it.voltage) }
        println("computeKs() R^2: ${regression.rSquare}")
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

        println("computeKa() R^2: ${regression.rSquare}")
        return regression.slope
    }

    fun getDifferentialDriveConstants(
        wheelRadius: Length,
        trackWidthRadius: Length,
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

        /**
         * Get a value for the linear acceleration by finding the maximum acceleration during the time interval.
         * Convert the radians per second to meters per second to use in the torque calculation.
         *
         * v = omega * r
         */
        val linearAcceleration =
            linearAccelerationData.maxBy { it.radPerSecPerSec }!!.radPerSecPerSec * wheelRadius.value

        /**
         * Get a value for the average of the absolute value of the angular accelerations for each wheel.
         */
        val avgAbsAngularAcceleration = angularAccelerationData.maxBy { it.radPerSecPerSec }!!.radPerSecPerSec

        /**
         * Next, use the average of the absolute value of the two wheel accelerations to compute the average
         * angular velocity in rad/s/s
         *
         * angular_acceleration = r(angular_right - angular_left)/d
         * angular_acceleration = r(angular_right - angular_left)/(2r)
         *
         * Here avgAbsAngularAcceleration = (angular_right - angular_left) / 2
         */
        val angularAcceleration = wheelRadius.value * avgAbsAngularAcceleration / trackWidthRadius.value

        /**
         * Calculate the torque required to accelerate in this linear straight-line case.
         *
         * torque = Force * radius
         * Force = mass * acceleration
         *
         * torque / radius = mass * acceleration
         */
        val torque = robotMass.value * wheelRadius.value * linearAcceleration

        /**
         * Assume the same amount of torque is available in the angular case.
         * Now compute the moment of inertia required for the measured angular acceleration.
         *
         * torque = moment of inertia * angular acceleration
         * moment of inertia = torque / angular acceleration
         */
        val momentOfInertia = torque / angularAcceleration

        return CharacterizationData(
            linearKv, computeKs(linearVelocityData),
            linearKa, angularKa, momentOfInertia
        )
    }

    data class CharacterizationData(
        val linearKv: Double,
        val linearKs: Double,
        val linearKa: Double,
        val angularKa: Double,
        val momentOfInertia: Double
    )
}