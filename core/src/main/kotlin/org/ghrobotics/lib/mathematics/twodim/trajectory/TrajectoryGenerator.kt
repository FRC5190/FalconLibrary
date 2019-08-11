/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.geometry.Rotation2d
import edu.wpi.first.wpilibj.geometry.Transform2d
import edu.wpi.first.wpilibj.geometry.Translation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2dWithCurvature
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.TrajectoryConstraint
import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derived.*
import kotlin.math.*

val DefaultTrajectoryGenerator = TrajectoryGenerator(
    2.0.inches,
    0.25.inches,
    5.0.degrees
)

class TrajectoryGenerator(
    private val kMaxDx: SIUnit<Meter>,
    private val kMaxDy: SIUnit<Meter>,
    private val kMaxDTheta: SIUnit<Radian>
) {

    val baseline = generateTrajectory(
        listOf(
            Pose2d(0.0.meters, 0.0.meters, Rotation2d.fromDegrees(0.0)),
            Pose2d(10.0.feet, 0.0.meters, Rotation2d.fromDegrees(0.0))
        ),
        listOf(),
        0.0.meters.velocity,
        0.0.meters.velocity,
        10.0.feet.velocity,
        4.0.feet.acceleration,
        false
    )

    @Suppress("LongParameterList")
    fun generateTrajectory(
        wayPoints: List<Pose2d>,
        constraints: List<TrajectoryConstraint>,
        startVelocity: SIUnit<LinearVelocity>,
        endVelocity: SIUnit<LinearVelocity>,
        maxVelocity: SIUnit<LinearVelocity>,
        maxAcceleration: SIUnit<LinearAcceleration>,
        reversed: Boolean
    ): Trajectory {
        val flipTransform = Transform2d(
            Translation2d(),
            Rotation2d.fromDegrees(180.0)
        )

        // Make theta normal for trajectory generation if path is trajectoryReversed.
        val newWayPoints = wayPoints.asSequence().map { point ->
            if (reversed) point + flipTransform else point
        }

        var points = trajectoryFromSplineWaypoints(newWayPoints)

        // After trajectory generation, flip theta back so it's relative to the field.
        // Also fix curvature.
        // Derivative of curvature should stay the same because the change in curvature will be the same.
        if (reversed) {
            points = points.map { state ->
                Pose2dWithCurvature(
                    pose = state.pose + flipTransform,
                    curvature = -state.curvature,
                    dkds = state.dkds
                )
            }
        }

        return timeParameterizeTrajectory(
            points,
            constraints,
            startVelocity.value,
            endVelocity.value,
            maxVelocity.value,
            maxAcceleration.value.absoluteValue,
            reversed
        )
    }

    private fun trajectoryFromSplineWaypoints(
        wayPoints: Sequence<Pose2d>
    ): List<Pose2dWithCurvature> {
        val splines = wayPoints.zipWithNext { a, b ->
            QuinticHermiteSpline(
                a,
                b
            )
        }.toMutableList()
        return QuinticHermiteSpline.parameterizeSplines(splines, kMaxDx, kMaxDy, kMaxDTheta)
    }

    @Suppress(
        "ComplexMethod",
        "LongMethod",
        "NestedBlockDepth",
        "ThrowsCount",
        "LongParameterList",
        "TooGenericExceptionThrown",
        "LoopWithTooManyJumpStatements"
    )
    private fun timeParameterizeTrajectory(
        points: List<Pose2dWithCurvature>,
        constraints: List<TrajectoryConstraint>,
        startVelocity: Double,
        endVelocity: Double,
        maxVelocity: Double,
        maxAcceleration: Double,
        reversed: Boolean
    ): Trajectory {

        val epsilon = 1E-6
        val constrainedPoses = Array(points.size) {
            ConstrainedPose(
                Pose2dWithCurvature(Pose2d(), 0.0, 0.0),
                0.0,
                0.0,
                0.0,
                0.0
            )
        }

        var predecessor = ConstrainedPose(
            points.first(),
            0.0,
            startVelocity,
            -maxAcceleration,
            maxAcceleration
        )

        constrainedPoses[0] = predecessor

        for (i in points.indices) {
            val constrainedPose: ConstrainedPose = constrainedPoses[i]

            // Begin constraining based on predecessor
            constrainedPose.state = points[i]
            val ds: Double = constrainedPose.state.distance(predecessor.state)
            constrainedPose.distance = predecessor.distance + ds

            // We may need to iterate to find the maximum end velocity and common acceleration, since acceleration
            // limits may be a function of velocity.
            while (true) {
                // Enforce global max velocity and max reachable velocity by global acceleration limit.
                // vf = sqrt(vi^2 + 2*a*d)
                constrainedPose.maxVelocity =
                    min(maxVelocity, sqrt(predecessor.maxVelocity.pow(2) + 2.0 * predecessor.maxAcceleration * ds))

                if (constrainedPose.maxVelocity.isNaN()) throw RuntimeException()

                // Enforce global max absolute acceleration.
                constrainedPose.minAcceleration = -maxAcceleration
                constrainedPose.maxAcceleration = maxAcceleration

                // At this point, the state is fully constructed, but no constraints have been applied aside from
                // predecessor state max accel

                // Enforce all velocity constraints.
                for (constraint in constraints) {
                    constrainedPose.maxVelocity = min(
                        constrainedPose.maxVelocity,
                        constraint.getMaxVelocity(constrainedPose.state)
                    )
                }

                // This should never happen if constraints are well-behaved.
                if (constrainedPose.maxVelocity < 0.0) throw RuntimeException()

                // Now enforce all acceleration constraints.
                enforceAccelerationLimits(reversed, constraints, constrainedPose)

                // This should never happen if constraints are well-behaved.
                if (constrainedPose.minAcceleration > constrainedPose.maxAcceleration) throw RuntimeException()

                if (ds < epsilon) break

                // If the max acceleration for this constraint state is more conservative than what we had applied, we
                // need to reduce the max accel at the predecessor state and try again.
                val actualAcceleration =
                    (constrainedPose.maxVelocity.pow(2) - predecessor.maxVelocity.pow(2)) / (2.0 * ds)

                if (constrainedPose.maxAcceleration < actualAcceleration - epsilon) {
                    predecessor.maxAcceleration = constrainedPose.maxAcceleration
                } else {
                    if (actualAcceleration > predecessor.minAcceleration + epsilon) {
                        predecessor.maxAcceleration = actualAcceleration
                    }
                    // If actual acceleration is less than predecessor min accel, we will repair during the backward
                    // pass.
                    break
                }
            }

            predecessor = constrainedPose
        }

        var successor = ConstrainedPose(
            points.last(),
            constrainedPoses.last().distance,
            endVelocity,
            -maxAcceleration,
            maxAcceleration
        )

        for (i in points.indices.reversed()) {
            val constrainedPose: ConstrainedPose = constrainedPoses[i]
            val ds: Double = constrainedPose.distance - successor.distance // will be negative

            while (true) {
                // Enforce reverse max reachable velocity limit.
                // vf = sqrt(vi^2 + 2*a*d), where vi = successor.
                val newMaxVelocity = sqrt(successor.maxVelocity.pow(2) + 2.0 * successor.minAcceleration * ds)

                // No new limits to impose.
                if (newMaxVelocity >= constrainedPose.maxVelocity) break

                constrainedPose.maxVelocity = newMaxVelocity
                if (constrainedPose.maxVelocity.isNaN()) throw RuntimeException()

                // Now check all acceleration constraints with the lower max velocity.
                enforceAccelerationLimits(reversed, constraints, constrainedPose)

                if (constrainedPose.minAcceleration > constrainedPose.maxAcceleration) throw RuntimeException()
                if (ds > epsilon) break

                // If the min acceleration for this constraint state is more conservative than what we have applied, we
                // need to reduce the min accel and try again.
                val actualAcceleration =
                    (constrainedPose.maxVelocity.pow(2) - successor.maxVelocity.pow(2)) / (2.0 * ds)

                if (constrainedPose.minAcceleration > actualAcceleration + epsilon) {
                    successor.minAcceleration = constrainedPose.minAcceleration
                } else {
                    successor.minAcceleration = actualAcceleration
                    break
                }
            }
            successor = constrainedPose
        }

        // Integrate the constrained states forward in time to obtain the TimedStates.
        val timedStates = ArrayList<Trajectory.TimedState>(points.size)
        var t = 0.0
        var s = 0.0
        var v = startVelocity

        for (i in points.indices) {
            val constrainedPose = constrainedPoses[i]

            // Advance t
            val ds = constrainedPose.distance - s
            val accel = (constrainedPose.maxVelocity.pow(2) - v.pow(2)) / (2.0 * ds)

            var dt = 0.0
            if (i > 0) {
                timedStates[i - 1] = timedStates[i - 1].copy(
                    acceleration = SIUnit(if (reversed) -accel else accel)
                )
                dt = when {
                    abs(accel) > epsilon -> (constrainedPose.maxVelocity - v) / accel
                    abs(v) > epsilon -> ds / v
                    else -> throw RuntimeException()
                }
            }
            t += dt
            if (t.isNaN() || t.isInfinite()) throw RuntimeException()

            v = constrainedPose.maxVelocity
            s = constrainedPose.distance

            timedStates.add(
                Trajectory.TimedState(
                    SIUnit(t),
                    SIUnit(if (reversed) -v else v),
                    SIUnit(if (reversed) -accel else accel),
                    constrainedPose.state
                )
            )
        }
        return Trajectory(timedStates, reversed)
    }

    private data class ConstrainedPose(
        var state: Pose2dWithCurvature,
        var distance: Double,
        var maxVelocity: Double,
        var minAcceleration: Double,
        var maxAcceleration: Double
    )

    private fun enforceAccelerationLimits(
        reverse: Boolean,
        constraints: List<TrajectoryConstraint>,
        constrainedPose: ConstrainedPose
    ) {
        for (constraint in constraints) {
            val minMaxAccel = constraint.getMinMaxAcceleration(
                constrainedPose.state,
                (if (reverse) -1.0 else 1.0) * constrainedPose.maxVelocity
            )
            if (!minMaxAccel.valid) {
                throw RuntimeException()
            }
            constrainedPose.minAcceleration = max(
                constrainedPose.minAcceleration,
                if (reverse) -minMaxAccel.maxAcceleration else minMaxAccel.minAcceleration
            )
            constrainedPose.maxAcceleration = min(
                constrainedPose.maxAcceleration,
                if (reverse) -minMaxAccel.minAcceleration else minMaxAccel.maxAcceleration
            )
        }
    }
}