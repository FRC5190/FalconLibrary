package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.mathematics.units.meter
import org.junit.Assert
import org.junit.Test

class TransformTests {
    @Test
    fun testTransforms() {
        // Position of the static object
        val staticObjectPose = Pose2d(10.meter, 10.meter, 0.degree)

        // Position of the camera on the robot.
        // Camera is on the back of the robot (1 foot behind the center)
        // Camera is facing backward
        val robotToCamera = Pose2d((-1).meter, 0.meter, 180.degree)

        // The camera detects the static object 9 meter in front and 2 meter to the right of it.
        val cameraToStaticObject = Pose2d(9.meter, 2.meter, 0.degree)

        // Transform the static object into the robot's coordinates
        val robotToStaticObject = robotToCamera + cameraToStaticObject

        // Get the global robot pose
        val globalRobotPose = staticObjectPose - robotToStaticObject

        println(
            "X: ${globalRobotPose.translation.x.meter}, Y: ${globalRobotPose.translation.y.meter}, " +
                "Angle: ${globalRobotPose.rotation.degree}"
        )

        Assert.assertEquals(0.0, globalRobotPose.translation.x, kEpsilon)
        Assert.assertEquals(8.0, globalRobotPose.translation.y, kEpsilon)
        Assert.assertEquals((-180).degree, globalRobotPose.rotation)
    }
}