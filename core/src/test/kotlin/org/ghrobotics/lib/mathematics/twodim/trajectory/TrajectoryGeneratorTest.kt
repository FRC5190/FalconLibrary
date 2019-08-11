/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.twodim.trajectory

import com.team254.lib.physics.DCMotorTransmission
import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Transform2d
import org.ghrobotics.lib.mathematics.twodim.geometry.mirror
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.AngularAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.DifferentialDriveDynamicsConstraint
import org.ghrobotics.lib.mathematics.units.derived.*
import org.ghrobotics.lib.mathematics.units.foot
import org.ghrobotics.lib.mathematics.units.inches
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.mathematics.units.seconds
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.math.pow

class TrajectoryGeneratorTest {

    companion object {
        private const val kRobotLinearInertia = 60.0 // kg
        private const val kRobotAngularInertia = 10.0 // kg m^2
        private const val kRobotAngularDrag = 12.0  // N*m / (rad/sec)
        private const val kDriveVIntercept = 1.055  // V
        private const val kDriveKv = 0.135  // V per rad/s
        private const val kDriveKa = 0.012  // V per rad/s^2

        private val kDriveWheelRadiusInches = 3.0.inches
        private val kWheelBaseDiameter = 29.5.inches

        private val transmission = DCMotorTransmission(
            speedPerVolt = 1 / kDriveKv,
            torquePerVolt = kDriveWheelRadiusInches.value.pow(2) * kRobotLinearInertia / (2.0 * kDriveKa),
            frictionVoltage = kDriveVIntercept
        )

        val drive = DifferentialDrive(
            mass = kRobotLinearInertia,
            moi = kRobotAngularInertia,
            angularDrag = kRobotAngularDrag,
            wheelRadius = kDriveWheelRadiusInches.value,
            effectiveWheelBaseRadius = kWheelBaseDiameter.value / 2.0,
            leftTransmission = transmission,
            rightTransmission = transmission
        )

        private val kMaxCentripetalAcceleration = 4.0.foot.acceleration
        private val kMaxAcceleration = 4.0.foot.acceleration
        private val kMaxAngularAcceleration = 2.0.radians.acceleration
        private val kMaxVelocity = 10.0.foot.velocity

        private const val kTolerance = 0.1

        private val kSideStart = Pose2d(1.54.foot, 23.234167.foot, 180.0.degrees.toRotation2d())
        private val kNearScaleEmpty = Pose2d(23.7.foot, 20.2.foot, 160.0.degrees.toRotation2d())

        val trajectory = DefaultTrajectoryGenerator.generateTrajectory(
            listOf(
                kSideStart,
                kSideStart + Transform2d((-13.0).foot, 0.0.foot, 0.0.degrees.toRotation2d()),
                kSideStart + Transform2d((-19.5).foot, 5.0.foot, (-90.0).degrees.toRotation2d()),
                kSideStart + Transform2d((-19.5).foot, 14.0.foot, (-90.0).degrees.toRotation2d()),
                kNearScaleEmpty.mirror()
            ),
            listOf(
                CentripetalAccelerationConstraint(kMaxCentripetalAcceleration),
                DifferentialDriveDynamicsConstraint(drive, 9.0.volts),
                AngularAccelerationConstraint(kMaxAngularAcceleration)
            ),
            0.0.foot.velocity,
            0.0.foot.velocity,
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