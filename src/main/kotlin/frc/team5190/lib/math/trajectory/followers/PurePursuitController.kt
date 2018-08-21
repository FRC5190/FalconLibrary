package frc.team5190.lib.math.trajectory.followers

import frc.team5190.lib.kEpsilon
import frc.team5190.lib.math.geometry.Pose2d
import frc.team5190.lib.math.geometry.Pose2dWithCurvature
import frc.team5190.lib.math.geometry.Twist2d
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.trajectory.TrajectoryIterator
import frc.team5190.lib.math.trajectory.timing.TimedState
import frc.team5190.lib.math.trajectory.view.TimedView
import kotlin.math.pow

// Reference tracking implementation of pure pursuit
// https://www.ri.cmu.edu/pub_files/pub3/coulter_r_craig_1992_1/coulter_r_craig_1992_1.pdf

class PurePursuitController(val trajectory: Trajectory<TimedState<Pose2dWithCurvature>>,
                            private val lookahead: Double = 0.3 /* seconds */) : TrajectoryFollower {

    private val iterator = TrajectoryIterator(TimedView(trajectory))

    override var point = iterator.preview(0.0)

    override val pose
        get() = point.state.state.pose

    override val isFinished
        get() = iterator.isDone


    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0

    // Returns desired linear and angular velocity of the robot
    override fun getSteering(robot: Pose2d, nanotime: Long): Twist2d {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9

        val goal = iterator.preview(lookahead).state.state.pose

        return calculateTwist(
                current = robot,
                goal = goal,
                pathV = this.point.state.velocity,
                pathW = this.point.state.velocity * this.point.state.state.curvature
        ).also { this.point = iterator.advance(dt) }

    }

    companion object {
        fun calculateTwist(current: Pose2d, goal: Pose2d, pathV: Double, pathW: Double): Twist2d {
            val relative = goal inFrameOfReferenceOf current
            val curvature = 2 * relative.translation.x / relative.translation.norm.pow(2) * if (pathV < kEpsilon) -1 else 1
            return if (curvature < 1.0) Twist2d(pathV, 0.0, curvature) else Twist2d(pathV, 0.0, pathW)
        }
    }
}