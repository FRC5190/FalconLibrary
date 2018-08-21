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

import frc.team5190.lib.math.geometry.Rotation2d

// Interface for Rotation
interface IRotation2d<S> : State<S> {
    // Rotation
    val rotation: Rotation2d
}
