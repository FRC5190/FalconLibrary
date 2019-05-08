package com.team254.lib.physics

import org.ghrobotics.lib.mathematics.kEpsilon

/*
 * Implementation from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

/**
 * Model of a DC motor rotating a shaft.  All parameters refer to the output (e.g. should already consider gearing
 * and efficiency losses).  The motor is assumed to be symmetric forward/reverse.
 */
class DCMotorTransmission(
    val speedPerVolt: Double, // rad/s per V (no load),
    private val torquePerVolt: Double, // N m per V (stall),
    val frictionVoltage: Double // V
) {

    /**
     * Returns the free speed of the motor at the specified voltage
     * @param voltage specified voltage
     * @return free speed
     */
    fun getFreeSpeedAtVoltage(voltage: Double): Double {
        return when {
            voltage > kEpsilon -> Math.max(0.0, voltage - frictionVoltage) * speedPerVolt
            voltage < -kEpsilon -> Math.min(0.0, voltage + frictionVoltage) * speedPerVolt
            else -> 0.0
        }
    }


    /**
     * Returns the torque produced by the motor
     * @param outputSpeed The speed that is being outputted by the motor
     * @param voltage The voltage through the motor
     * @return torque
     */
    fun getTorqueForVoltage(outputSpeed: Double, voltage: Double): Double {
        var effectiveVoltage = voltage
        when {
            outputSpeed > kEpsilon -> // Forward motion, rolling friction.
                effectiveVoltage -= frictionVoltage
            outputSpeed < -kEpsilon -> // Reverse motion, rolling friction.
                effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> // System is static, forward torque.
                effectiveVoltage = Math.max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> // System is static, reverse torque.
                effectiveVoltage = Math.min(0.0, voltage + frictionVoltage)
            else -> // System is idle.
                return 0.0
        }
        return torquePerVolt * (-outputSpeed / speedPerVolt + effectiveVoltage)
    }


    /**
     * Returns the voltage going through the motor
     * @param outputSpeed The speed that is being outputted by the motor
     * @param torque Torque produced by the motor
     * @return voltage
     */
    fun getVoltageForTorque(outputSpeed: Double, torque: Double): Double {
        val fv: Double = when {
            outputSpeed > kEpsilon -> // Forward motion, rolling friction.
                frictionVoltage
            outputSpeed < -kEpsilon -> // Reverse motion, rolling friction.
                -frictionVoltage
            torque > kEpsilon -> // System is static, forward torque.
                frictionVoltage
            torque < -kEpsilon -> // System is static, reverse torque.
                -frictionVoltage
            else -> // System is idle.
                return 0.0
        }
        return torque / torquePerVolt + outputSpeed / speedPerVolt + fv
    }
}
