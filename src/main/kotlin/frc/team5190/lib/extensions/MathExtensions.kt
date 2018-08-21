/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.extensions

import frc.team5190.lib.kEpsilon
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D
import org.apache.commons.math3.linear.Array2DRowRealMatrix
import kotlin.math.PI
import kotlin.math.absoluteValue

infix fun Double.epsilonEquals(other: Double): Boolean {
    return (this - other).absoluteValue < kEpsilon
}

infix fun Double.cos(other: Double): Double {
    return this * Math.cos(other)
}

infix fun Double.sin(other: Double): Double {
    return this * Math.sin(other)
}

fun Double.boundRadians(): Double {
    var x = this
    while (x >= PI) x -= (2 * PI)
    while (x < -PI) x += (2 * PI)
    return x
}

fun Double.boundDegrees(): Double {
    var x = this
    while (x >= 180.0) x -= 360.0
    while (x < -180.0) x += 360.0
    return x
}



operator fun Vector2D.minus(other: Vector2D): Vector2D = Vector2D(this.x - other.x, this.y - other.y)
operator fun Vector2D.plus(other: Vector2D): Vector2D = add(other)
operator fun Vector2D.times(scalar: Double): Vector2D = scalarMultiply(scalar)
operator fun Vector2D.div(scalar: Double): Vector2D = scalarMultiply(1 / scalar)
operator fun Vector2D.unaryPlus(): Vector2D = Vector2D(+this.x, +this.y)
operator fun Vector2D.unaryMinus(): Vector2D = Vector2D(-this.x, -this.y)

operator fun Vector2D.get(index: Int) =
        if (index == 0) x else y

typealias Matrix = Array2DRowRealMatrix

operator fun Matrix.plus(other: Matrix) = this.add(other)!!
operator fun Matrix.minus(other: Matrix) = this.subtract(other)!!
operator fun Matrix.times(other: Matrix) = this.multiply(other)!!

