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

import org.ghrobotics.lib.mathematics.State
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d

// Interface for Rotation
interface IRotation2d<S> : State<S> {
    // Rotation
    val rotation: Rotation2d
}
