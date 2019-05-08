package org.ghrobotics.lib.subsystems.drive.characterization

import org.ghrobotics.lib.types.CSVWritable

/**
 * Stores data that is used during drive characterization.
 * This data should be imported into Excel.
 * Find acceleration from velocity and dt and then run a voltage vs. velocity + acceleration multiple regression.
 *
 * @param voltage Voltage being outputted to the motors
 * @param velocity Velocity of the robot in rad/s
 * @param dt Time since last data entry in seconds
 */
data class CharacterizationData(
    val voltage: Double, // volts
    val velocity: Double, // radians per second
    val dt: Double // seconds
) : CSVWritable {
    override fun toCSV(): String = "$voltage,$velocity,$dt"
}

/*
 * "How to find moment of inertia you ask?"
 * Here is how.
 *
 * Find the force in the linear case.
 * Force = linear mass * linear acceleration
 *
 * Find the torque
 * Torque = Force * wheel radius
 *
 * Assume the same amount of torque is available in the angular case.
 * Torque = angular mass * angular acceleration
 * Torque = moment of inertia * angular acceleration (moi = angular mass)
 * Torque is now distributed along the wheelbase, not the wheel radius
 *
 * Torque (from linear case) / wheel radius * effective wheel base radius = moment of inertia * angular acceleration
 *
 * You can also enter your information in this spreadsheet and get the moment of inertia...
 * https://drive.google.com/open?id=1GxCjF9x4W-ypqXcSIjEALYtKZOlcYAnU
 */