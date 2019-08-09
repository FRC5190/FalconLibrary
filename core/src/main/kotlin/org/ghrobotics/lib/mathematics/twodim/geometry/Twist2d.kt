/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

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
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.meter
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

class Twist2d constructor(
    val dx: SIUnit<Meter>,
    val dy: SIUnit<Meter>,
    val dTheta: SIUnit<Radian>
) {

    val norm get() = if (dy.value == 0.0) dx.absoluteValue else hypot(dx.value, dy.value).meter

    val asPose: Pose2d
        get() {
            val dTheta = this.dTheta.value
            val sinTheta = sin(dTheta)
            val cosTheta = cos(dTheta)

            val (s, c) = if (abs(dTheta) < kEpsilon) {
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