/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive

class FeedForwardController(drive: DifferentialDrive) : RamseteController(drive, 0.0, 0.0)