/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.mathematics.twodim.trajectory

import frc.team5190.lib.mathematics.twodim.geometry.Pose2d
import frc.team5190.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import frc.team5190.lib.mathematics.twodim.geometry.Rotation2d
import frc.team5190.lib.mathematics.twodim.geometry.interfaces.State
import frc.team5190.lib.mathematics.twodim.polynomials.ParametricQuinticHermiteSpline
import frc.team5190.lib.mathematics.twodim.polynomials.ParametricSpline
import frc.team5190.lib.mathematics.twodim.polynomials.ParametricSplineGenerator
import frc.team5190.lib.mathematics.twodim.trajectory.constraints.TimingConstraint
import frc.team5190.lib.mathematics.twodim.trajectory.view.DistanceView
import koma.pow

object TrajectoryGenerator {

    private const val kMaxDx = 2.0 / 12.0
    private const val kMaxDy = 0.25 / 12.0
    private val kMaxDTheta = Math.toDegrees(5.0)


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
                        dcurvature_ds = -state.dkds
                )
            })
        }

        // Parameterize by time and return.
        return timeParameterizeTrajectory(reversed, DistanceView(trajectory), kMaxDx, constraints,
                startVel, endVel, maxVelocity, maxAcceleration)
    }

    private fun trajectoryFromSplineWaypoints(waypoints: List<Pose2d>, maxDx: Double, maxDy: Double, maxDTheta: Double): Trajectory<Pose2dWithCurvature> {
        val splines = java.util.ArrayList<ParametricQuinticHermiteSpline>(waypoints.size - 1)
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
    private fun <S : State<S>> timeParameterizeTrajectory(reversed: Boolean, distanceView: DistanceView<S>,
                                                          stepSize: Double, constraints: List<TimingConstraint<S>>,
                                                          startVel: Double, endVel: Double,
                                                          maxVel: Double, maxAbsAcceleration: Double): Trajectory<TimedState<S>> {

        fun getAccelerationLimits(reversed: Boolean, constraints: List<TimingConstraint<S>>,
                                  state: S, velocity: Double,
                                  minAcceleration: Double, maxAcceleration: Double): TimingConstraint.MinMaxAcceleration {
            var min = minAcceleration
            var max = maxAcceleration

            for (constraint in constraints) {
                val limits = constraint.getMinMaxAcceleration(state, if (reversed) -1.0 else 1.0 * velocity)
                if (!limits.valid) throw RuntimeException()

                min = min.coerceAtLeast(if (reversed) -1.0 else 1.0 * limits.minAcceleration)
                max = max.coerceAtMost(if (reversed) -1.0 else .0 * limits.maxAcceleration)
            }
            return TimingConstraint.MinMaxAcceleration(min, max)
        }

        // Compute states to use during parameterization
        val distanceViewRange = distanceView.firstInterpolant..distanceView.lastInterpolant
        val distanceViewSteps = Math.ceil((distanceView.lastInterpolant - distanceView.firstInterpolant) / stepSize + 1)

        val states = (0 until distanceViewSteps.toInt()).map { step ->
            distanceView.sample((step * stepSize + distanceView.firstInterpolant).coerceIn(distanceViewRange)).state
        }

        // Class that holds a constrained state
        data class ConstrainedState<S : State<S>>(var state: S, var distance: Double,
                                                  var maxVelocity: Double, var minAcceleration: Double,
                                                  var maxAcceleration: Double)

        val constrainedStates = ArrayList<ConstrainedState<S>>(states.size)
        val kEpsilon = 1E-6

        // Forward pass. We look at pairs of consecutive states, where the start state has already been velocity
        // parameterized (though we may adjust the velocity downwards during the backwards pass). We wish to find an
        // acceleration that is admissible at both the start and end state, as well as an admissible end velocity. If
        // there is no admissible end velocity or acceleration, we set the end velocity to the state's maximum allowed
        // velocity and will repair the acceleration during the backward pass (by slowing down the predecessor).

        var predecessor = ConstrainedState(states[0], 0.0, startVel, -maxAbsAcceleration, +maxAbsAcceleration)
        states.forEachIndexed { index, state ->

            val ds = state.distance(predecessor.state)
            val distance = predecessor.distance + ds

            // We may need to iterate to find the maximum end velocity and common acceleration, since acceleration
            // limits may be a function of velocity.
            while (true) {
                // Enforce velocity limit through acceleration limit
                // v^2 = v0^2 + 2ax
                var _maxVelocity = Math.sqrt(predecessor.maxVelocity.pow(2) + 2 * predecessor.maxAcceleration * ds).coerceAtMost(maxVel)

                var _minAcceleration = -maxAbsAcceleration
                var _maxAcceleration = +maxAbsAcceleration

                // Enforce constraints
                for (constraint in constraints) {
                    _maxVelocity = _maxVelocity.coerceAtMost(constraint.getMaxVelocity(state))
                }
                if (_maxVelocity < 0) throw RuntimeException()

                val accelerationLimits = getAccelerationLimits(reversed, constraints, state,
                        _maxVelocity, _minAcceleration, _maxAcceleration)

                if (!accelerationLimits.valid) throw RuntimeException()

                _minAcceleration = accelerationLimits.minAcceleration
                _maxAcceleration = accelerationLimits.maxAcceleration

                if (ds < kEpsilon) {
                    constrainedStates.add(ConstrainedState(state, distance, _maxVelocity, _minAcceleration, _maxAcceleration))
                    break
                }

                // If the max acceleration for this constraint state is more conservative than what we had applied, we
                // need to reduce the max accel at the predecessor state and try again.
                val actualAcceleration = (_maxVelocity.pow(2) - predecessor.maxVelocity.pow(2)) / (2.0 * ds)
                if (_maxAcceleration < actualAcceleration - kEpsilon) {
                    predecessor.maxAcceleration = _maxAcceleration
                } else {
                    if (actualAcceleration > predecessor.minAcceleration + kEpsilon) {
                        predecessor.maxAcceleration = actualAcceleration
                    }
                    constrainedStates.add(ConstrainedState(state, distance, _maxVelocity, _minAcceleration, _maxAcceleration))
                    break
                }
            }
            predecessor = constrainedStates[index]
        }

        // Backward Pass
        var successor = ConstrainedState(states[states.size - 1], constrainedStates[states.size - 1].distance,
                endVel, -maxAbsAcceleration, +maxAbsAcceleration)

        states.indices.reversed().forEach { i ->
            val constrainedState = constrainedStates[i]
            val ds = constrainedState.distance - successor.distance

            while (true) {
                // Enforce max velocity limit
                val newMaxVelocity = Math.sqrt(successor.maxVelocity.pow(2) + 2.0 * successor.minAcceleration * ds)
                if (newMaxVelocity >= constrainedState.maxVelocity) break

                constrainedState.maxVelocity = newMaxVelocity
                if (constrainedState.maxVelocity.isNaN()) throw RuntimeException()

                val accelerationLimits = getAccelerationLimits(reversed, constraints, constrainedState.state,
                        constrainedState.maxVelocity, constrainedState.minAcceleration, constrainedState.maxAcceleration)

                if (!accelerationLimits.valid) throw RuntimeException()

                constrainedState.minAcceleration = accelerationLimits.minAcceleration
                constrainedState.maxAcceleration = accelerationLimits.maxAcceleration

                if (ds > kEpsilon) break

                val actualAcceleration = (constrainedState.maxVelocity.pow(2) - successor.maxVelocity.pow(2)) / (2.0 * ds)
                if (constrainedState.minAcceleration > actualAcceleration + kEpsilon) {
                    successor.minAcceleration = constrainedState.minAcceleration
                } else {
                    successor.minAcceleration = actualAcceleration
                    break
                }
            }
            successor = constrainedState
        }

        // Integrate
        val timedStates = ArrayList<TimedState<S>>(states.size)
        var t = 0.0
        var s = 0.0
        var v = 0.0

        states.indices.forEach { i ->
            val constrainedState = constrainedStates[i]

            // Advance t.
            val ds = constrainedState.distance - s
            val accel = (constrainedState.maxVelocity.pow(2) - v.pow(2)) / (2.0 * ds)
            var dt = 0.0
            if (i > 0) {
                timedStates[i - 1].acceleration = (if (reversed) -accel else accel)

                dt = when {
                    Math.abs(accel) > kEpsilon -> (constrainedState.maxVelocity - v) / accel
                    Math.abs(v) > kEpsilon -> ds / v
                    else -> throw RuntimeException()
                }
            }
            t += dt
            if (java.lang.Double.isNaN(t) || java.lang.Double.isInfinite(t)) {
                throw RuntimeException()
            }

            v = constrainedState.maxVelocity
            s = constrainedState.distance
            timedStates.add(TimedState(constrainedState.state, t, if (reversed) -v else v, if (reversed) -accel else accel))
        }
        return Trajectory(timedStates)
    }
}