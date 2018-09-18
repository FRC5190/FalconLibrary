/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package org.ghrobotics.lib.mathematics.twodim.trajectory

import org.ghrobotics.lib.mathematics.State
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.twodim.polynomials.ParametricQuinticHermiteSpline
import org.ghrobotics.lib.mathematics.twodim.polynomials.ParametricSpline
import org.ghrobotics.lib.mathematics.twodim.polynomials.ParametricSplineGenerator
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.TimingConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.view.DistanceView
import kotlin.math.pow

object TrajectoryGenerator {

    private const val kMaxDx = 2.0 / 12.0
    private const val kMaxDy = 0.25 / 12.0
    private val kMaxDTheta = Math.toRadians(5.0)


    // Generate trajectory with custom start and end velocity.
    fun generateTrajectory(
            reversed: Boolean,
            wayPoints: List<Pose2d>,
            constraints: List<TimingConstraint<Pose2dWithCurvature>>,
            startVel: Double,
            endVel: Double,
            maxVelocity: Double,
            maxAcceleration: Double
    ): Trajectory<TimedState<Pose2dWithCurvature>>? {

        val flippedPose2d = Pose2d.fromRotation(Rotation2d.fromDegrees(180.0))

        // Make theta normal for trajectory generation if path is trajectoryReversed.
        val newWayPoints = wayPoints.map { if (reversed) it.transformBy(flippedPose2d) else it }

        var trajectory = trajectoryFromSplineWaypoints(newWayPoints, kMaxDx, kMaxDy, kMaxDTheta)

        // After trajectory generation, flip theta back so it's relative to the field.
        // Also fix curvature and its derivative
        if (reversed) {
            trajectory = Trajectory(trajectory.map { state ->
                Pose2dWithCurvature(
                        pose = state.pose.transformBy(flippedPose2d),
                        curvature = -state.curvature,
                        dkds = -state.dkds
                )
            })
        }

        // Parameterize by time and return.
        return timeParameterizeTrajectory(reversed, DistanceView(trajectory), kMaxDx, constraints,
                startVel, endVel, maxVelocity, maxAcceleration)
    }

    private fun trajectoryFromSplineWaypoints(waypoints: List<Pose2d>,
                                              maxDx: Double,
                                              maxDy: Double,
                                              maxDTheta: Double): Trajectory<Pose2dWithCurvature> {

        val splines = ArrayList<ParametricQuinticHermiteSpline>(waypoints.size - 1)
        for (i in 1 until waypoints.size) {
            splines.add(ParametricQuinticHermiteSpline(waypoints[i - 1], waypoints[i]))
        }
        ParametricQuinticHermiteSpline.optimizeSpline(splines)
        return trajectoryFromSplines(splines, maxDx, maxDy, maxDTheta)
    }

    private fun trajectoryFromSplines(splines: List<ParametricSpline>, maxDx: Double,
                                      maxDy: Double, maxDTheta: Double): Trajectory<Pose2dWithCurvature> {
        return Trajectory(ParametricSplineGenerator.parameterizeSplines(splines, maxDx, maxDy,
                maxDTheta))
    }

    // http://www2.informatik.uni-freiburg.de/~lau/students/Sprunk2008.pdf and Team 254
    private fun <S : State<S>> timeParameterizeTrajectory(reverse: Boolean,
                                                          distanceView: DistanceView<S>,
                                                          stepSize: Double,
                                                          constraints: List<TimingConstraint<S>>,
                                                          startVelocity: Double,
                                                          endVelocity: Double,
                                                          maxVelocity: Double,
                                                          maxAbsAcceleration: Double): Trajectory<TimedState<S>> {

        class ConstrainedState<S : State<S>> {
            lateinit var state: S
            var distance: Double = 0.0
            var maxVelocity: Double = 0.0
            var minAcceleration: Double = 0.0
            var maxAcceleration: Double = 0.0


            override fun toString(): String {
                return state.toString() + ", distance: " + distance + ", maxVelocity: " + maxVelocity + ", " +
                        "minAcceleration: " + minAcceleration + ", maxAcceleration: " + maxAcceleration
            }
        }

        fun enforceAccelerationLimits(reverse: Boolean, constraints: List<TimingConstraint<S>>,
                                      constraintState: ConstrainedState<S>) {

            for (constraint in constraints) {
                val minMaxAccel = constraint.getMinMaxAcceleration(
                        constraintState.state,
                        (if (reverse) -1.0 else 1.0) * constraintState.maxVelocity)
                if (!minMaxAccel.valid) {
                    throw RuntimeException()
                }
                constraintState.minAcceleration = Math.max(constraintState.minAcceleration,
                        if (reverse) -minMaxAccel.maxAcceleration else minMaxAccel.minAcceleration)
                constraintState.maxAcceleration = Math.min(constraintState.maxAcceleration,
                        if (reverse) -minMaxAccel.minAcceleration else minMaxAccel.maxAcceleration)
            }

        }

        val distanceViewRange = distanceView.firstInterpolant..distanceView.lastInterpolant
        val distanceViewSteps = Math.ceil((distanceView.lastInterpolant - distanceView.firstInterpolant) / stepSize + 1).toInt()

        val states = (0 until distanceViewSteps).map { step ->
            distanceView.sample((step * stepSize + distanceView.firstInterpolant).coerceIn(distanceViewRange)).state
        }

        val constraintStates = ArrayList<ConstrainedState<S>>(states.size)
        val epsilon = 1E-6

        // Forward pass. We look at pairs of consecutive states, where the start state has already been velocity
        // parameterized (though we may adjust the velocity downwards during the backwards pass). We wish to find an
        // acceleration that is admissible at both the start and end state, as well as an admissible end velocity. If
        // there is no admissible end velocity or acceleration, we set the end velocity to the state's maximum allowed
        // velocity and will repair the acceleration during the backward pass (by slowing down the predecessor).

        var predecessor = ConstrainedState<S>()
        predecessor.state = states[0]
        predecessor.distance = 0.0
        predecessor.maxVelocity = startVelocity
        predecessor.minAcceleration = -maxAbsAcceleration
        predecessor.maxAcceleration = maxAbsAcceleration


        for (i in states.indices) {
            // Add the new state.
            constraintStates.add(ConstrainedState())
            val constraintState = constraintStates[i]
            constraintState.state = states[i]
            val ds = constraintState.state.distance(predecessor.state)
            constraintState.distance = ds + predecessor.distance

            // We may need to iterate to find the maximum end velocity and common acceleration, since acceleration
            // limits may be a function of velocity.
            while (true) {
                // Enforce global max velocity and max reachable velocity by global acceleration limit.
                // vf = sqrt(vi^2 + 2*a*d)
                constraintState.maxVelocity = Math.min(maxVelocity,
                        Math.sqrt(predecessor.maxVelocity.pow(2) + 2.0 * predecessor.maxAcceleration * ds))
                if (java.lang.Double.isNaN(constraintState.maxVelocity)) {
                    throw RuntimeException()
                }
                // Enforce global max absolute acceleration.
                constraintState.minAcceleration = -maxAbsAcceleration
                constraintState.maxAcceleration = maxAbsAcceleration

                // At this point, the state is full constructed, but no constraints have been applied aside from
                // predecessor
                // state max accel.

                // Enforce all velocity constraints.
                for (constraint in constraints) {
                    constraintState.maxVelocity = Math.min(constraintState.maxVelocity,
                            constraint.getMaxVelocity(constraintState.state))
                }
                if (constraintState.maxVelocity < 0.0) {
                    // This should never happen if constraints are well-behaved.
                    throw RuntimeException()
                }

                // Now enforce all acceleration constraints.
                enforceAccelerationLimits(reverse, constraints, constraintState)
                if (constraintState.minAcceleration > constraintState.maxAcceleration) {
                    // This should never happen if constraints are well-behaved.
                    throw RuntimeException()
                }

                if (ds < epsilon) {
                    break
                }

                // If the max acceleration for this constraint state is more conservative than what we had applied, we
                // need to reduce the max accel at the predecessor state and try again.
                val actualAcceleration = (constraintState.maxVelocity.pow(2) - predecessor.maxVelocity.pow(2)) / (2.0 * ds)
                if (constraintState.maxAcceleration < actualAcceleration - epsilon) {
                    predecessor.maxAcceleration = constraintState.maxAcceleration
                } else {
                    if (actualAcceleration > predecessor.minAcceleration + epsilon) {
                        predecessor.maxAcceleration = actualAcceleration
                    }
                    // If actual acceleration is less than predecessor min accel, we will repair during the backward
                    // pass.
                    break
                }
                // System.out.println("(intermediate) i: " + i + ", " + constraint_state.toString());
            }
            // System.out.println("i: " + i + ", " + constraint_state.toString());
            predecessor = constraintState
        }

        // Backward pass.
        var successor = ConstrainedState<S>()
        successor.state = states[states.size - 1]
        successor.distance = constraintStates[states.size - 1].distance
        successor.maxVelocity = endVelocity
        successor.minAcceleration = -maxAbsAcceleration
        successor.maxAcceleration = maxAbsAcceleration
        for (i in states.indices.reversed()) {
            val constraintState = constraintStates[i]
            val ds = constraintState.distance - successor.distance // will be negative.

            while (true) {
                // Enforce reverse max reachable velocity limit.
                // vf = sqrt(vi^2 + 2*a*d), where vi = successor.
                val newMaxVelocity = Math.sqrt(successor.maxVelocity.pow(2) + 2.0 * successor.minAcceleration * ds)
                if (newMaxVelocity >= constraintState.maxVelocity) {
                    // No new limits to impose.
                    break
                }
                constraintState.maxVelocity = newMaxVelocity
                if (java.lang.Double.isNaN(constraintState.maxVelocity)) {
                    throw RuntimeException()
                }

                // Now check all acceleration constraints with the lower max velocity.
                enforceAccelerationLimits(reverse, constraints, constraintState)
                if (constraintState.minAcceleration > constraintState.maxAcceleration) {
                    throw RuntimeException()
                }

                if (ds > epsilon) {
                    break
                }
                // If the min acceleration for this constraint state is more conservative than what we have applied, we
                // need to reduce the min accel and try again.
                val actualAcceleration = (constraintState.maxVelocity.pow(2) - successor.maxVelocity.pow(2)) / (2.0 * ds)
                if (constraintState.minAcceleration > actualAcceleration + epsilon) {
                    successor.minAcceleration = constraintState.minAcceleration
                } else {
                    successor.minAcceleration = actualAcceleration
                    break
                }
            }
            successor = constraintState
        }

        // Integrate the constrained states forward in time to obtain the TimedStates.
        val timedStates = ArrayList<TimedState<S>>(states.size)
        var t = 0.0
        var s = 0.0
        var v = 0.0
        for (i in states.indices) {
            val constrainedState = constraintStates[i]
            // Advance t.
            val ds = constrainedState.distance - s
            val accel = (constrainedState.maxVelocity.pow(2) - v.pow(2)) / (2.0 * ds)
            var dt = 0.0
            if (i > 0) {
                timedStates[i - 1].acceleration = (if (reverse) -accel else accel)

                dt = when {
                    Math.abs(accel) > epsilon -> (constrainedState.maxVelocity - v) / accel
                    Math.abs(v) > epsilon -> ds / v
                    else -> throw RuntimeException()
                }
            }
            t += dt
            if (t.isNaN()|| t.isInfinite()) {
                throw RuntimeException()
            }

            v = constrainedState.maxVelocity
            s = constrainedState.distance
            timedStates.add(TimedState(constrainedState.state, t, if (reverse) -v else v, if (reverse) -accel else accel))
        }
        return Trajectory(timedStates)
    }
}