package org.ghrobotics.lib.mathematics.units.derived

import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
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
fun Rotation2d.toUnbounded() = SIUnit<Radian>(radian)