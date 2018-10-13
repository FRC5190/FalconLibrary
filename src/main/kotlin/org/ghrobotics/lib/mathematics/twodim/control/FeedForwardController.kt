/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedTrajectory

class FeedForwardController(trajectory: TimedTrajectory<Pose2dWithCurvature>) : RamseteController(trajectory, 0.0, 0.0)