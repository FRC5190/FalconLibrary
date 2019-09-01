/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Transform2d
import org.ghrobotics.lib.mathematics.twodim.geometry.mirror
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.AngularAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.DifferentialDriveKinematicsConstraint
import org.ghrobotics.lib.mathematics.units.derived.acceleration
import org.ghrobotics.lib.mathematics.units.derived.degrees
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.derived.toRotation2d
import org.ghrobotics.lib.mathematics.units.derived.velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.mathematics.units.seconds
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.math.pow

class TrajectoryGeneratorTest {

    companion object {
        val kinematics = DifferentialDriveKinematics(0.381)

        private val kMaxCentripetalAcceleration = 4.0.feet.acceleration
        private val kMaxAcceleration = 4.0.feet.acceleration
        private val kMaxAngularAcceleration = 2.0.radians.acceleration
        private val kMaxVelocity = 10.0.feet.velocity

        private const val kTolerance = 0.1

        private val kSideStart = Pose2d(1.54.feet, 23.234167.feet, 180.0.degrees.toRotation2d())
        private val kNearScaleEmpty = Pose2d(23.7.feet, 20.2.feet, 160.0.degrees.toRotation2d())

        val trajectory = DefaultTrajectoryGenerator.generateTrajectory(
            listOf(
                kSideStart,
                kSideStart + Transform2d((-13.0).feet, 0.0.feet, 0.0.degrees.toRotation2d()),
                kSideStart + Transform2d((-19.5).feet, 5.0.feet, (-90.0).degrees.toRotation2d()),
                kSideStart + Transform2d((-19.5).feet, 14.0.feet, (-90.0).degrees.toRotation2d()),
                kNearScaleEmpty.mirror()
            ),
            listOf(
                CentripetalAccelerationConstraint(kMaxCentripetalAcceleration),
                AngularAccelerationConstraint(kMaxAngularAcceleration),
                DifferentialDriveKinematicsConstraint(kinematics, 9.feet.velocity)
            ),
            0.0.feet.velocity,
            0.0.feet.velocity,
            kMaxVelocity,
            kMaxAcceleration,
            true
        )
    }

    @Test
    fun testTrajectoryGenerationAndConstraints() {
        val iterator = trajectory

        var time = 0.0.seconds
        val dt = 20.0.milli.seconds

        while (!iterator.isDone) {
            val pt = iterator.advance(dt)
            time += dt

            val wheelSpeeds = kinematics.toWheelSpeeds(
                ChassisSpeeds(
                    pt.velocity.value, 0.0, pt.velocity.value * pt.state
                        .curvature
                )
            )

            assert(wheelSpeeds.leftMetersPerSecond <= 2.7432 + kTolerance)
            assert(wheelSpeeds.rightMetersPerSecond <= 2.7432 + kTolerance)

            val ac = pt.velocity.value.pow(2) * pt.state.curvature

            assert(ac <= kMaxCentripetalAcceleration.value + kTolerance)
            assert(pt.velocity.value.absoluteValue < kMaxVelocity.value + kTolerance)
            assert(pt.acceleration.value.absoluteValue < kMaxAcceleration.value + kTolerance)

            assert(
                pt.acceleration.value * pt.state.curvature +
                    pt.velocity.value.pow(2) * pt.state.dkds
                    < kMaxAngularAcceleration.value + kTolerance
            )
        }
    }
}