/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.geometry.Rotation3d
import edu.wpi.first.math.geometry.Translation3d
import edu.wpi.first.util.sendable.Sendable
import edu.wpi.first.util.sendable.SendableBuilder
import kotlin.math.atan

abstract class AbstractFalconGyro : Sendable {
    abstract fun roll(): Double
    abstract fun pitch(): Double
    abstract fun yaw(): Double
    abstract fun odometryYaw(): Double

    fun inclination(): Rotation2d {
        // Our robot is conceptually a rectangle, and a rectangle is
        // basically a square, and squares are planes.  So think of
        // robot rotation as defining a plane.  Our goal is to find the
        // angle between the robot plane and the ground, going in the X
        // direction.
        //
        // A plane is defined uniquely by a point on it and a vector
        // (starting at the point) perpendicular to the plane --- that
        // is, pointing up.  The point will be (0,0,0) and the vector
        // will be (0,0,1).  We rotate by yaw, pitch, and roll to find
        // the actual current value of the vector.
        //
        // To find the angle of the plane along the X axis, we calculate
        // a point on the plane (1, 0, z).  If the rotated vector ends
        // at (a, b, c):
        //     ax + by + cz = 0
        // Substituting and rearranging:
        //     a(1) + b(0) + cz = 0
        //     a + cz = 0
        //     cz = -a
        //     z = -a/c
        val rotation = Rotation3d(roll(), pitch(), odometryYaw())
        val upVector = Translation3d(0.0, 0.0, 1.0).rotateBy(rotation)
        val z: Double = -upVector.x / upVector.z
        return Rotation2d(atan(z))
    }

    override fun initSendable(builder: SendableBuilder?) {
        builder!!
        builder.addDoubleProperty("Odometry Yaw (deg)", { Math.toDegrees(odometryYaw()) }, {})
        builder.addDoubleProperty("Yaw (deg)", { yaw() }, {})
        builder.addDoubleProperty("Pitch (deg)", { pitch() }, {})
        builder.addDoubleProperty("Roll (deg)", { roll() }, {})
        builder.addDoubleProperty("Inclination (deg)", { inclination().degrees }, {})
    }
}
