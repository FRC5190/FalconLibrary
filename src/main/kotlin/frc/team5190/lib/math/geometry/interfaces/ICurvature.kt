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

// Interface for curvature
interface ICurvature<S> : State<S> {
    // Curvature
    val curvature: Double

    // Derivative of Curvature
    val dkds: Double
}
