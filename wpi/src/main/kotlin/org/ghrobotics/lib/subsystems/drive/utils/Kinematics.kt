/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.utils

import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.subsystems.drive.swerve.FalconSwerveDrivetrain
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.hypot

class Kinematics(val driveTrain: FalconSwerveDrivetrain) {
    private val L: Double = driveTrain.wheelbase
    private val W: Double = driveTrain.trackWidth
    private val R = hypot(L, W)

    /**
     * Forward kinematics using only encoders
     */
//    fun forwardKinematics(drive_signal: DriveSignal): Twist2d {
//        return forwardKinematics(drive_signal.getWheelSpeeds(), drive_signal.getWheelAzimuths())
//    }

    /**
     * @param wheel_speeds
     * @param wheel_azimuths
     * @return Twist2d representing forward, strafe, and angular velocities in real world units
     */
//    fun forwardKinematics(wheel_speeds: DoubleArray, wheel_azimuths: Array<Rotation2d>): Twist2d {
//        val vx = DoubleArray(4) // wheel velocities in the x (forward) direction
//        val vy = DoubleArray(4) // wheel velocities in the y (strafe) direction
//        for (i in vx.indices) {
//            vx[i] = wheel_azimuths[i].cos() * wheel_speeds[i]
//            vy[i] = wheel_azimuths[i].sin() * wheel_speeds[i]
//        }
//
//        // average possible solutions to minimize error
//        val A = (vy[2] + vy[3]) / 2
//        val B = (vy[0] + vy[1]) / 2
//        val C = (vx[0] + vx[3]) / 2
//        val D = (vx[1] + vx[2]) / 2
//
//        // average possible solutions to minimize error
//        val forward = (C + D) / 2
//        val strafe = (A + B) / 2
//        val rotation = ((strafe - A) * R / L + (B - strafe) * R / L + (forward - C) * R / W
//            + (D - forward) * R / W) / 4
//        return Twist2d(forward, strafe, rotation)
//    }

    /**
     * Use Gyro for dtheta
     */
//    fun forwardKinematics(
//        drive_signal: DriveSignal, prev_heading: Rotation2d,
//        current_heading: Rotation2d?, dt: Double
//    ): Twist2d {
//        val ret_val: Twist2d = forwardKinematics(drive_signal)
//        return Twist2d(ret_val.dx, ret_val.dy, prev_heading.inverse().rotateBy(current_heading).getRadians() / dt)
//    }

//    fun forwardKinematics(
//        wheel_speeds: DoubleArray, wheel_azimuths: Array<Rotation2d>, prev_heading: Rotation2d,
//        current_heading: Rotation2d?, dt: Double
//    ): Twist2d {
//        val ret_val: Twist2d = forwardKinematics(wheel_speeds, wheel_azimuths)
//        return Twist2d(ret_val.dx, ret_val.dy, prev_heading.inverse().rotateBy(current_heading).getRadians() / dt)
//    }

    /**
     * For convenience, integrate forward kinematics with a Twist2d and previous
     * rotation.
     */
//    fun integrateForwardKinematics(current_pose: Pose2d, forward_kinematics: Twist2d): Pose2d {
//        return current_pose.transformBy(
//            Pose2d(
//                forward_kinematics.dx, forward_kinematics.dy,
//                Rotation2d.fromRadians(forward_kinematics.dtheta)
//            )
//        )
//    }

    fun inverseKinematics(
        forward: Double,
        strafe: Double,
        rotation: Double,
        field_relative: Boolean,
    ): DriveSignal {
        return inverseKinematics(forward, strafe, rotation, field_relative, true)
    }

    fun inverseKinematics(
        forward: Double,
        strafe: Double,
        rotation: Double,
        field_relative: Boolean,
        normalize_outputs: Boolean,
    ): DriveSignal {
        var forward = forward
        var strafe = strafe
        if (field_relative) {
            val gyroHeading = Rotation2d(driveTrain.swerveDriveIO.gyro().radians, true)
            val temp: Double = forward * gyroHeading.cos() + strafe * gyroHeading.sin()
            strafe = -forward * gyroHeading.sin() + strafe * gyroHeading.cos()
            forward = temp
        }
        val A = strafe - rotation * L / R
        val B = strafe + rotation * L / R
        val C = forward - rotation * W / R
        val D = forward + rotation * W / R
        val wheel_speeds = DoubleArray(4)
        wheel_speeds[0] = hypot(B, C)
        wheel_speeds[1] = hypot(B, D)
        wheel_speeds[2] = hypot(A, D)
        wheel_speeds[3] = hypot(A, C)

        // normalize wheel speeds if above 1
        if (normalize_outputs) {
            var max_speed = 1.0
            for (i in wheel_speeds.indices) {
                if (abs(wheel_speeds[i]) > max_speed) {
                    max_speed = abs(wheel_speeds[i])
                }
            }
            for (i in wheel_speeds.indices) {
                wheel_speeds[i] /= max_speed
            }
        }
        val wheel_azimuths: Array<Rotation2d> = Array<Rotation2d>(4) { Rotation2d() }
        wheel_azimuths[0] = Rotation2d.fromRadians(atan2(B, C))
        wheel_azimuths[1] = Rotation2d.fromRadians(atan2(B, D))
        wheel_azimuths[2] = Rotation2d.fromRadians(atan2(A, D))
        wheel_azimuths[3] = Rotation2d.fromRadians(atan2(A, C))
        return DriveSignal(wheel_speeds, wheel_azimuths)
    }
}
