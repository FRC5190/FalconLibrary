package org.ghrobotics.lib.mathematics.threedim.geometry

import kotlin.math.asin
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

data class Quaternion(
    val x: Double,
    val y: Double,
    val z: Double,
    val w: Double
) {
    val eulerAngles: Vector3
        get() {
            val sqw = w * w
            val sqx = x * x
            val sqy = y * y
            val sqz = z * z
            val unit = sqx + sqy + sqz + sqw // if normalised is one, otherwise is correction factor
            val test = x * y + z * w
            if (test >= 0.5 * unit) { // singularity at north pole
                return Vector3(
                    2.0 * atan2(x, w),
                    Math.PI / 2.0,
                    0.0
                )
            }
            if (test <= -0.5 * unit) { // singularity at south pole
                return Vector3(
                    -2.0 * atan2(x, w),
                    -Math.PI / 2.0,
                    0.0
                )
            }
            return Vector3(
                atan2(2.0 * y * w - 2.0 * x * z, sqx - sqy - sqz + sqw),
                asin(2.0 * test / unit),
                atan2(2.0 * x * w - 2.0 * y * z, -sqx + sqy - sqz + sqw)
            )
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

    operator fun plus(other: Quaternion) =
        Quaternion(
            x + other.x,
            y + other.y,
            z + other.z,
            w + other.w
        )

    operator fun times(other: Quaternion) =
        Quaternion(
            x * other.w + y * other.z - z * other.y + w * other.x,
            -x * other.z + y * other.w + z * other.x + w * other.y,
            x * other.y - y * other.x + z * other.w + w * other.z,
                -x * other.x - y * other.y - z * other.z + w * other.w
        )

    fun transform(other: Vector3) =
        Vector3(
            w * w * other.x + 2.0 * y * w * other.z - 2.0 * z * w * other.y + x * x * other.x + 2.0 * y * x * other.y + 2.0 * z * x * other.z - z * z * other.x - y * y * other.x,
            2.0 * x * y * other.x + y * y * other.y + 2.0 * z * y * other.z + 2.0 * w * z * other.x - z * z * other.y + w * w * other.y - 2.0 * x * w * other.z - x * x * other.y,
            2.0 * x * z * other.x + 2.0 * y * z * other.y + z * z * other.z - 2.0 * w * y * other.x - y * y * other.z + 2.0 * w * x * other.y - x * x * other.z + w * w * other.z
        )

    companion object {
        val kIdentity = Quaternion(0.0, 0.0, 0.0, 1.0)

        /**
         * Creates a [Quaternion] from Euler Angles in radians
         */
        fun fromEulerAngles(yaw: Double, pitch: Double, roll: Double): Quaternion {
            val c1 = cos(yaw / 2)
            val c2 = cos(pitch / 2)
            val c3 = cos(roll / 2)
            val s1 = sin(yaw / 2)
            val s2 = sin(pitch / 2)
            val s3 = sin(roll / 2)

            return Quaternion(
                s1 * s2 * c3 + c1 * c2 * s3,
                s1 * c2 * c3 + c1 * s2 * s3,
                c1 * s2 * c3 - s1 * c2 * s3,
                c1 * c2 * c3 - s1 * s2 * s3
            )
        }

        fun fromAxisAngle(angle: Double, axis: Vector3): Quaternion {
            val s = Math.sin(angle * 0.5)
            return Quaternion(
                axis.x * s,
                axis.y * s,
                axis.z * s,
                Math.cos(angle * 0.5)
            )
        }
    }
}