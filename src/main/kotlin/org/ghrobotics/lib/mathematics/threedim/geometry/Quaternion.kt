package org.ghrobotics.lib.mathematics.threedim.geometry

import kotlin.math.absoluteValue
import kotlin.math.withSign

data class Quaternion(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double
) {
    val eulerAngles: Vector3
        get() {
            // roll (x-axis rotation)
            val sinr_cosp = 2.0 * (w * x + y * z)
            val cosr_cosp = 1.0 - 2.0 * (x * x + y * y)
            val roll = Math.atan2(sinr_cosp, cosr_cosp)

            // pitch (y-axis rotation)
            val sinp = 2.0 * (w * y - z * x)
            val pitch = if (sinp.absoluteValue >= 1.0) {
                (Math.PI / 2.0).withSign(sinp) // use 90 degrees if out of range
            } else {
                Math.asin(sinp)
            }

            // yaw (z-axis rotation)
            val siny_cosp = 2.0 * (w * z + x * y)
            val cosy_cosp = 1.0 - 2.0 * (y * y + z * z)
            val yaw = Math.atan2(siny_cosp, cosy_cosp)

            return Vector3(roll, pitch, yaw)
        }

    val norm get() = Math.sqrt(x * x + y * y + z * z + w * w)

    val normalized: Quaternion
        get() {
            val norm = norm
            return Quaternion(
                x / norm,
                y / norm,
                z / norm,
                w / norm
            )
        }

    operator fun get(componentId: Int) =
        when (componentId) {
            0 -> x
            1 -> y
            2 -> z
            3 -> w
            else -> throw IndexOutOfBoundsException()
        }

    companion object {
        val kIdentity = Quaternion(0.0, 0.0, 0.0, 1.0)

        fun fromEulerAngles(yaw: Double, pitch: Double, roll: Double): Quaternion {
            val cy = Math.cos(yaw * 0.5)
            val sy = Math.sin(yaw * 0.5)
            val cp = Math.cos(pitch * 0.5)
            val sp = Math.sin(pitch * 0.5)
            val cr = Math.cos(roll * 0.5)
            val sr = Math.sin(roll * 0.5)

            return Quaternion(
                cy * cp * sr - sy * sp * cr,
                sy * cp * sr + cy * sp * cr,
                sy * cp * cr - cy * sp * sr,
                cy * cp * cr + sy * sp * sr
            )
        }
    }
}