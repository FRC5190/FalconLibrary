package org.ghrobotics.lib.mathematics.threedim

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Quaternion
import org.ghrobotics.lib.mathematics.threedim.geometry.Vector3
import org.junit.Test

class GeometryTests {

    @Test
    fun testQuaternionMul() {
        val one = Quaternion.fromEulerAngles(Math.toRadians(45.0), 0.0, 0.0)
        val two = Quaternion.fromEulerAngles(Math.toRadians(25.0), 0.0, 0.0)

        val three = one * two

        assert(Math.toDegrees(three.eulerAngles.x) epsilonEquals 70.0)
    }

    @Test
    fun testPose3dAdd() {
        val drive = Pose3d(
            Vector3(0.5, 0.25, 0.5),
            Quaternion.fromEulerAngles(Math.toRadians(0.0), 0.0, 0.0)
        )
        val elevator = Pose3d(
            Vector3(0.0, 1.25, 0.0),
            Quaternion.fromEulerAngles(0.0, Math.toRadians(25.0), 0.0)
        )
        val arm = Pose3d(
            Vector3(0.75, 0.0, 0.0),
            Quaternion.fromEulerAngles(0.0, 0.0, 0.0)
        )

        val actualArmPose = drive + elevator + arm

        assert(
            actualArmPose.translation epsilonEquals Vector3(
                1.1797308402774875,
                1.8169636963055247,
                0.5
            )
        )
    }

}