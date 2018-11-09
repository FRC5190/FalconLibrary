package org.ghrobotics.lib.subsystems.drive.characterization

import org.ghrobotics.lib.types.CSVWritable

data class CharacterizationData(
    val voltage: Double, // volts
    val velocity: Double, // radians per second
    val dt: Double // seconds
) : CSVWritable {
    override fun toCSV(): String = "$voltage,$velocity,$dt"
}