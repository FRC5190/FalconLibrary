/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import kotlin.math.PI
import kotlin.math.absoluteValue

fun Double.lerp(endValue: Double, t: Double) = this + (endValue - this) * t.coerceIn(0.0, 1.0)

fun <T : Comparable<T>> min(a: T, b: T) = if (a < b) a else b
fun <T : Comparable<T>> max(a: T, b: T) = if (a > b) a else b

infix fun Double.epsilonEquals(other: Double) = minus(other).absoluteValue < kEpsilon

infix fun Double.cos(other: Double) = times(Math.cos(other))

infix fun Double.sin(other: Double) = times(Math.sin(other))

fun Double.boundRadians(): Double {
    var x = this
    while (x >= PI) x -= (2 * PI)
    while (x < -PI) x += (2 * PI)
    return x
}
