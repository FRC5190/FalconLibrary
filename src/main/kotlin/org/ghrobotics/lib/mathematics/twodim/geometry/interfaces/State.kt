/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.geometry.interfaces

import org.ghrobotics.lib.types.CSVWritable
import org.ghrobotics.lib.types.Interpolable

// Interface that holds all states
interface State<S> : Interpolable<S>, CSVWritable {

    // Distance to another state
    fun distance(other: S): Double

    // Default equals, toString, and toCSV methods to implement
    override fun equals(other: Any?): Boolean
    override fun toString(): String
    override fun toCSV(): String
}
