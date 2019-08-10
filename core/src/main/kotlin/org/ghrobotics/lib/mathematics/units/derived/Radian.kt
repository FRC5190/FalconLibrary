/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.units.derived

import edu.wpi.first.wpilibj.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.Unitless

typealias Radian = Unitless

val Double.radian get() = SIUnit<Radian>(this)
val Double.degree get() = SIUnit<Radian>(Math.toRadians(this))

val Number.radian get() = toDouble().radian
val Number.degree get() = toDouble().degree

val SIUnit<Radian>.radian get() = value
val SIUnit<Radian>.degree get() = Math.toDegrees(value)

fun SIUnit<Radian>.toRotation2d() = Rotation2d(radian)
fun Rotation2d.toUnbounded() = SIUnit<Radian>(radians)