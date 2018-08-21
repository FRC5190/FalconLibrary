/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.trajectory.timing

import frc.team5190.lib.math.trajectory.view.DistanceView
import frc.team5190.lib.math.trajectory.Trajectory
import frc.team5190.lib.math.geometry.interfaces.State

import java.util.ArrayList

object TimingUtil {

    fun <S : State<S>> timeParameterizeTrajectory(
            reverse: Boolean,
            distanceView: DistanceView<S>,
            stepSize: Double,
            constraints: List<TimingConstraint<S>>,
            startVelocity: Double,
            endVelocity: Double,
            maxVelocity: Double,
            maxAbsAcceleration: Double): Trajectory<TimedState<S>> {


        val numStates = Math.ceil(distanceView.lastInterpolant / stepSize + 1).toInt()
        val states = ArrayList<S>(numStates)
        for (i in 0 until numStates) {
            states.add(distanceView.sample(Math.min(i * stepSize, distanceView.lastInterpolant)).state)
        }
        return timeParameterizeTrajectory(reverse, states, constraints, startVelocity, endVelocity,
                maxVelocity, maxAbsAcceleration)

    }

    private fun <S : State<S>> timeParameterizeTrajectory(
            reverse: Boolean,
            states: List<S>,
            constraints: List<TimingConstraint<S>>,
            startVelocity: Double,
            endVelocity: Double,
            maxVelocity: Double,
            maxAbsAcceleration: Double): Trajectory<TimedState<S>> {

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
                        Math.sqrt(predecessor.maxVelocity * predecessor.maxVelocity + 2.0 * predecessor.maxAcceleration * ds))
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
                duplicateFunction(reverse, constraints, constraintState)
                if (constraintState.minAcceleration > constraintState.maxAcceleration) {
                    // This should never happen if constraints are well-behaved.
                    throw RuntimeException()
                }

                if (ds < epsilon) {
                    break
                }
                // If the max acceleration for this constraint state is more conservative than what we had applied, we
                // need to reduce the max accel at the predecessor state and try again.
                // TODO: Simply using the new max acceleration is guaranteed to be valid, but may be too conservative.
                // Doing a search would be better.
                val actualAcceleration = (constraintState.maxVelocity * constraintState.maxVelocity - predecessor.maxVelocity * predecessor.maxVelocity) / (2.0 * ds)
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
                val newMaxVelocity = Math.sqrt(successor.maxVelocity * successor.maxVelocity + 2.0 * successor.minAcceleration * ds)
                if (newMaxVelocity >= constraintState.maxVelocity) {
                    // No new limits to impose.
                    break
                }
                constraintState.maxVelocity = newMaxVelocity
                if (java.lang.Double.isNaN(constraintState.maxVelocity)) {
                    throw RuntimeException()
                }

                // Now check all acceleration constraints with the lower max velocity.
                duplicateFunction(reverse, constraints, constraintState)
                if (constraintState.minAcceleration > constraintState.maxAcceleration) {
                    throw RuntimeException()
                }

                if (ds > epsilon) {
                    break
                }
                // If the min acceleration for this constraint state is more conservative than what we have applied, we
                // need to reduce the min accel and try again.
                // TODO: Simply using the new min acceleration is guaranteed to be valid, but may be too conservative.
                // Doing a search would be better.
                val actualAcceleration = (constraintState.maxVelocity * constraintState.maxVelocity - successor.maxVelocity * successor.maxVelocity) / (2.0 * ds)
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
            val accel = (constrainedState.maxVelocity * constrainedState.maxVelocity - v * v) / (2.0 * ds)
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
            if (java.lang.Double.isNaN(t) || java.lang.Double.isInfinite(t)) {
                throw RuntimeException()
            }

            v = constrainedState.maxVelocity
            s = constrainedState.distance
            timedStates.add(TimedState(constrainedState.state, t, if (reverse) -v else v, if (reverse) -accel else accel))
        }
        return Trajectory(timedStates)
    }

    private fun <S : State<S>> duplicateFunction(reverse: Boolean, constraints: List<TimingConstraint<S>>, constraint_state: ConstrainedState<S>) {
        for (constraint in constraints) {
            val minMaxAccel = constraint.getMinMaxAcceleration(
                    constraint_state.state,
                    (if (reverse) -1.0 else 1.0) * constraint_state.maxVelocity)
            if (!minMaxAccel.valid) {
                throw RuntimeException()
            }
            constraint_state.minAcceleration = Math.max(constraint_state.minAcceleration,
                    if (reverse) -minMaxAccel.maxAcceleration else minMaxAccel.minAcceleration)
            constraint_state.maxAcceleration = Math.min(constraint_state.maxAcceleration,
                    if (reverse) -minMaxAccel.minAcceleration else minMaxAccel.maxAcceleration)
        }
    }

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
}
