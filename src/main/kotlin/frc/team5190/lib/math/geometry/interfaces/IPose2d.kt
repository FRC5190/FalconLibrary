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

import frc.team5190.lib.math.geometry.Pose2d

// Interface for position on the field
interface IPose2d<S> : IRotation2d<S>, ITranslation2d<S> {
    // Pose
    val pose: Pose2d

    // Function for pose transformation
    fun transformBy(transform: Pose2d): S

    // Mirroring poses
    val mirror: S
}
