package org.ghrobotics.lib.mathematics.onedim.control

import kotlin.math.pow

class SCurveController(val distance: Double, maxVelocity: Double,
                       private val maxAcceleration: Double, private val jerk: Double) : IKinematicController {

    private var cruiseVelocity: Double
    private val maxAccelTime = maxAcceleration / jerk

    private val shortProfile: Boolean = maxVelocity * (maxAccelTime + maxVelocity / maxAcceleration) > distance

    private val t1 = TXVData()
    private val t2 = TXVData()
    private val t3 = TXVData()
    private val t4 = TXVData()
    private val t5 = TXVData()
    private val t6 = TXVData()
    private val t7 = TXVData()

    init {
        cruiseVelocity = if (shortProfile) {
            maxAcceleration * (
                    Math.sqrt(distance / maxAcceleration - 0.75 * maxAccelTime.pow(2)) - 0.5 * maxAccelTime
                    )
        } else {
            maxVelocity
        }

        t1.t = maxAccelTime
        t2.t = cruiseVelocity / maxAcceleration
        t3.t = t2.t + maxAccelTime

        t4.t = if (shortProfile) {
            t3.t
        } else {
            distance / cruiseVelocity
        }

        t5.t = t4.t + maxAccelTime
        t6.t = t4.t + t2.t
        t7.t = t6.t + maxAccelTime


        // Concave up
        t1.x = x(t1.t, 0.0, 0.0, 0.0, jerk)
        t1.v = v(t1.t, 0.0, 0.0, jerk)

        // Linear
        t2.x = x(t2.t - t1.t, t1.x, t1.v, maxAcceleration, 0.0)
        t2.v = v(t2.t - t1.t, t1.v, maxAcceleration, 0.0)

        // Concave down
        t3.x = x(t3.t - t2.t, t2.x, t2.v, maxAcceleration, -jerk)
        t3.v = v(t3.t - t2.t, t2.v, maxAcceleration, -jerk)

        // Cruise
        t4.x = x(t4.t - t3.t, t3.x, t3.v, 0.0, 0.0)
        t4.v = v(t4.t - t3.t, t3.v, 0.0, 0.0)

        // Concave down
        t5.x = x(t5.t - t4.t, t4.x, t4.v, 0.0, -jerk)
        t5.v = v(t5.t - t4.t, t4.v, 0.0, -jerk)

        // Linear
        t6.x = x(t6.t - t5.t, t5.x, t5.v, -maxAcceleration, 0.0)
        t6.v = v(t6.t - t5.t, t5.v, -maxAcceleration, 0.0)

        // Concave up
        t7.x = x(t7.t - t6.t, t6.x, t6.v, -maxAcceleration, jerk)
        t7.v = v(t7.t - t6.t, t6.v, -maxAcceleration, jerk)
    }

    // Loops
    private var lastCallTime = -1.0
    private var dt = -1.0
    private var elapsed = 0.0


    override fun getVelocity(nanotime: Long): PVAData {
        dt = if (lastCallTime < 0) 0.0 else nanotime / 1E9 - lastCallTime
        lastCallTime = nanotime / 1E9
        elapsed += dt

        return when {
            elapsed < t1.t -> {
                val t = elapsed
                PVAData(
                        x(t, 0.0, 0.0, 0.0, jerk),
                        v(t, 0.0, 0.0, jerk),
                        a(t, 0.0, 0.0)
                )
            }
            elapsed < t2.t -> {
                val t = elapsed - t1.t
                PVAData(
                        x(t, t1.x, t1.v, maxAcceleration, 0.0),
                        v(t, t1.v, maxAcceleration, 0.0),
                        a(t, maxAcceleration, 0.0)
                )
            }
            elapsed < t3.t -> {
                val t = elapsed - t2.t
                PVAData(
                        x(t, t2.x, t2.v, maxAcceleration, -jerk),
                        v(t, t2.v, maxAcceleration, -jerk),
                        a(t, maxAcceleration, -jerk)
                )
            }
            elapsed < t4.t -> {
                val t = elapsed - t3.t
                PVAData(
                        x(t, t3.x, t3.v, 0.0, 0.0),
                        v(t, t3.v, 0.0, 0.0),
                        a(t, 0.0, 0.0)
                )
            }
            elapsed < t5.t -> {
                val t = elapsed - t4.t
                PVAData(
                        x(t, t4.x, t4.v, 0.0, -jerk),
                        v(t, t4.v, 0.0, -jerk),
                        a(t, 0.0, -jerk)
                )
            }
            elapsed < t6.t -> {
                val t = elapsed - t5.t
                PVAData(
                        x(t, t5.x, t5.v, -maxAcceleration, 0.0),
                        v(t, t5.v, -maxAcceleration, 0.0),
                        a(t, -maxAcceleration, 0.0)
                )
            }
            elapsed < t7.t -> {
                val t = elapsed - t6.t
                PVAData(
                        x(t, t6.x, t6.v, -maxAcceleration, jerk),
                        v(t, t6.v, -maxAcceleration, jerk),
                        a(t, -maxAcceleration, jerk)
                )
            }
            else -> PVAData(distance, 0.0, 0.0)
        }
    }

    private fun x(t: Double, x0: Double, v0: Double, a0: Double, j: Double) =
            x0 + (v0 * t) + (0.5 * a0 * t.pow(2)) + (0.1667 * j * t.pow(3))

    private fun v(t: Double, v0: Double, a0: Double, j: Double) = v0 + a0 * t + 0.5 * j * t.pow(2)
    private fun a(t: Double, a0: Double, j: Double) = a0 + j * t

    private data class TXVData(var t: Double = 0.0, var x: Double = 0.0, var v: Double = 0.0)
}