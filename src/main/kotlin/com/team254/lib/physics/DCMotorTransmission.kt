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
        // All units must be SI!
        val speedPerVolt: Double  // rad/s per V (no load)
        ,
        private val torquePerVolt: Double  // N m per V (stall)
        ,
        val frictionVoltage: Double  // V
) {

    fun getFreeSpeedAtVoltage(voltage: Double): Double {
        return when {
            voltage > kEpsilon -> Math.max(0.0, voltage - frictionVoltage) * speedPerVolt
            voltage < kEpsilon -> Math.min(0.0, voltage + frictionVoltage) * speedPerVolt
            else -> 0.0
        }
    }

    fun getTorqueForVoltage(output_speed: Double, voltage: Double): Double {
        var effectiveVoltage = voltage
        when {
            output_speed > kEpsilon -> // Forward motion, rolling friction.
                effectiveVoltage -= frictionVoltage
            output_speed < -kEpsilon -> // Reverse motion, rolling friction.
                effectiveVoltage += frictionVoltage
            voltage > kEpsilon -> // System is static, forward torque.
                effectiveVoltage = Math.max(0.0, voltage - frictionVoltage)
            voltage < -kEpsilon -> // System is static, reverse torque.
                effectiveVoltage = Math.min(0.0, voltage + frictionVoltage)
            else -> // System is idle.
                return 0.0
        }
        return torquePerVolt * (-output_speed / speedPerVolt + effectiveVoltage)
    }

    fun getVoltageForTorque(output_speed: Double, torque: Double): Double {
        val frictionVoltage2: Double = when {
            output_speed > kEpsilon -> // Forward motion, rolling friction.
                frictionVoltage
            output_speed < -kEpsilon -> // Reverse motion, rolling friction.
                -frictionVoltage
            torque > kEpsilon -> // System is static, forward torque.
                frictionVoltage
            torque < -kEpsilon -> // System is static, reverse torque.
                -frictionVoltage
            else -> // System is idle.
                return 0.0
        }
        return torque / torquePerVolt + output_speed / speedPerVolt + frictionVoltage2
    }
}
