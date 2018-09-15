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

import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d

// Interface for Translation
interface ITranslation2d<S> : State<S> {
    // Translation
    val translation: Translation2d
}
