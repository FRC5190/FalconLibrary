/*
 * FRC Team 5190
 * Green Hope Falcons
 */

/*
 * Some implementations and algorithms borrowed from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


package frc.team5190.lib.math.geometry

import java.text.DecimalFormat


class Twist2d(val dx: Double = 0.0, val dy: Double = 0.0, val dtheta: Double = 0.0) {

    fun scaled(scale: Double): Twist2d {
        return Twist2d(dx * scale, dy * scale, dtheta * scale)
    }

    fun norm(): Double {
        // Common case of dy == 0
        return if (dy == 0.0) Math.abs(dx) else Math.hypot(dx, dy)
    }

    override fun toString(): String {
        val fmt = DecimalFormat("#0.000")
        return "(" + fmt.format(dx) + "," + fmt.format(dy) + "," + fmt.format(Math.toDegrees(dtheta)) + " deg)"
    }
}