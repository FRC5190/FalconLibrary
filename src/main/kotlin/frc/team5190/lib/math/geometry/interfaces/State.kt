/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package frc.team5190.lib.math.geometry.interfaces

import frc.team5190.lib.types.CSVWritable
import frc.team5190.lib.types.Interpolable

// Interface that holds all states
interface State<S> : Interpolable<S>, CSVWritable {

    // Distance to another state
    fun distance(other: S): Double

    // Default equals, toString, and toCSV methods to implement
    override fun equals(other: Any?): Boolean
    override fun toString(): String
    override fun toCSV(): String
}
