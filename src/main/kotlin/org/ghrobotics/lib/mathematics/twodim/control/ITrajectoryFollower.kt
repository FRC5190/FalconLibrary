/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.control

import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TimedEntry
import org.ghrobotics.lib.mathematics.twodim.trajectory.types.TrajectorySamplePoint
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nanosecond

interface ITrajectoryFollower {
    fun getSteering(robot: Pose2d, currentTime: Time = System.nanoTime().nanosecond): Twist2d
    val point: TrajectorySamplePoint<TimedEntry<Pose2dWithCurvature>>
    val pose: Pose2d
    val isFinished: Boolean
}