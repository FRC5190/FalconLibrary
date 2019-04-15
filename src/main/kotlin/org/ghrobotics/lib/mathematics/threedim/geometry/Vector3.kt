package org.ghrobotics.lib.mathematics.threedim.geometry

import org.ghrobotics.lib.mathematics.epsilonEquals

class Vector3(
    val x: Double,
    val y: Double,
    val z: Double
) {

    val magnitude get() = Math.sqrt(sqrMagnitude)
    val sqrMagnitude get() = x * x + y * y + z * z

    operator fun get(componentId: Int) =
        when (componentId) {
            0 -> x
            1 -> y
            2 -> z
            else -> throw IndexOutOfBoundsException()
        }

    infix fun epsilonEquals(other: Vector3) = x epsilonEquals other.x &&
        y epsilonEquals other.y && z epsilonEquals other.z

    companion object {
        val kZero = Vector3(0.0, 0.0, 0.0)
    }
}