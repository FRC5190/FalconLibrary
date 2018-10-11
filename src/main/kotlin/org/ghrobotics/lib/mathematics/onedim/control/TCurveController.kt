package org.ghrobotics.lib.mathematics.onedim.control

import kotlin.math.pow

@Suppress("MemberVisibilityCanBePrivate", "unused")
class TCurveController(val distance: Double,
                       val maxVelocity: Double, val maxAcceleration: Double) : IKinematicController {

    private var cruiseVelocity = maxVelocity

    private val tAccel: Double
    private val xAccel: Double

    private val tCruise: Double
    private val xCruise: Double

    init {
        tAccel = (maxVelocity / maxAcceleration).let {
            if (x(it, 0.0, 0.0, maxAcceleration) < distance / 2) {
                it
            } else {
                cruiseVelocity = maxAcceleration * Math.sqrt(distance / maxAcceleration)
                cruiseVelocity / maxAcceleration
            }
        }
        xAccel = x(tAccel, 0.0, 0.0, maxAcceleration)

        tCruise = ((distance - (2 * xAccel)) / cruiseVelocity).coerceAtLeast(0.0)
        xCruise = x(tCruise, 0.0, cruiseVelocity, 0.0)
    }

    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0
    private var t = 0.0

    override fun getVelocity(nanotime: Long): PVAData {

        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9
        t += dt

        return when {
            t < tAccel -> {
                val t1 = t
                PVAData(
                        x(t1, 0.0, 0.0, maxAcceleration),
                        v(t1, 0.0, maxAcceleration),
                        maxAcceleration)
            }
            t < tAccel + tCruise -> {
                val t2 = t - tAccel
                PVAData(
                        x(t2, xAccel, cruiseVelocity, 0.0),
                        v(t2, cruiseVelocity, 0.0),
                        0.0
                )
            }
            t < tAccel + tCruise + tAccel -> {
                val t3 = t - tAccel - tCruise
                PVAData(
                        x(t3, xAccel + xCruise, cruiseVelocity, -maxAcceleration),
                        v(t3, cruiseVelocity, -maxAcceleration),
                        -maxAcceleration
                )
            }
            else -> PVAData(distance, 0.0, 0.0)
        }
    }

    private fun x(t: Double, x0: Double, v0: Double, a: Double) = x0 + v0 * t + 0.5 * a * t.pow(2)
    private fun v(t: Double, v0: Double, a: Double) = v0 + a * t
}