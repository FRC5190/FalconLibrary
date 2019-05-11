/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */

package org.ghrobotics.lib.mathematics.twodim.geometry

import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import kotlin.math.absoluteValue

class Twist2d constructor(
    val dx: Double,
    val dy: Double,
    val dTheta: Rotation2d
) {

    constructor(
        dx: Length,
        dy: Length,
        dTheta: Rotation2d
    ) : this(dx.value, dy.value, dTheta)

    val norm get() = if (dy == 0.0) dx.absoluteValue else Math.hypot(dx, dy)

    val asPose: Pose2d
        get() {
            val dTheta = this.dTheta.radian
            val sinTheta = Math.sin(dTheta)
            val cosTheta = Math.cos(dTheta)

            val (s, c) = if (Math.abs(dTheta) < kEpsilon) {
                1.0 - 1.0 / 6.0 * dTheta * dTheta to .5 * dTheta
            } else {
                sinTheta / dTheta to (1.0 - cosTheta) / dTheta
            }
            return Pose2d(
                Translation2d(dx * s - dy * c, dx * c + dy * s),
                Rotation2d(cosTheta, sinTheta, false)
            )
        }

    operator fun times(scale: Double) =
        Twist2d(dx * scale, dy * scale, dTheta * scale)
}