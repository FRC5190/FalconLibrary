/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.TrajectorySamplePoint
import org.ghrobotics.lib.mathematics.twodim.trajectory.TimedState

interface TrajectoryFollower {
    fun getSteering(robot: Pose2d, nanotime: Long = System.nanoTime()): Twist2d
    val point: TrajectorySamplePoint<TimedState<Pose2dWithCurvature>>
    val pose: Pose2d
    val isFinished: Boolean
}