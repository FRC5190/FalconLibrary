/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory

class FeedForwardController(trajectory: TimedTrajectory<Pose2dWithCurvature>,
                            drive: DifferentialDrive) : RamseteController(trajectory, drive, 0.0, 0.0)