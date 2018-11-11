/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive

/**
 * Follows a path purely based on linear and angular velocities from the path without any external
 * disturbance correction.
 */
class FeedForwardController(drive: DifferentialDrive) : RamseteController(drive, 0.0, 0.0)