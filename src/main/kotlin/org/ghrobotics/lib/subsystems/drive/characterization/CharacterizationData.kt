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