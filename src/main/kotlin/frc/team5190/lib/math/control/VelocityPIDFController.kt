/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.math.control

import frc.team5190.lib.extensions.epsilonEquals
import java.util.concurrent.TimeUnit
import kotlin.math.absoluteValue
import kotlin.math.sign

// PID Controller that follows velocity setpoints
class VelocityPIDFController(private val kP: Double = 0.0,
                             private val kI: Double = 0.0,
                             private val kD: Double = 0.0,
                             private val kV: Double = 0.0,
                             private val kA: Double = 0.0,
                             private val kS: Double = 0.0,
                             private val kILimit: Double = 0.0,
                             private val kDeadband: Double = 0.1,
                             private val current: () -> Double) {


    // Stores PID related variables
    private var lastError = 0.0
    private var derivative = 0.0
    private var integral = 0.0


    // Looping related variables
    private var lastCallTime = -1.0
    private var dt = -1.0

    // Returns PID output between -1 and 1
    fun getPIDFOutput(target: Pair<Double, Double>): Double {
        // Store target
        val (targetVelocity, targetAcceleration) = target

        // Retrieve current position
        val current = current()

        // Get current time
        val timeSeconds = System.nanoTime() / 1.0e+9
        dt = if (lastCallTime < 0) {
            lastCallTime = timeSeconds
            return 0.0
        } else timeSeconds - lastCallTime


        // Calculate error
        val error = targetVelocity - current

        // Calculate integral and derivative terms
        integral += error * dt
        derivative += (error - lastError) / dt

        // Enforce I Limit
        if (integral > kILimit && (kILimit epsilonEquals 0.0).not()) integral = kILimit

        // Enforce Deadband
        if (targetVelocity.absoluteValue < kDeadband) return 0.0

        // Calculate feedback and feedforward terms
        val feedback = (kP * error) + (kI * integral) + (kD * derivative)
        val feedfrwd = (kV * targetVelocity) + (kA * targetAcceleration) + (kS * sign(targetVelocity))

        // Store last loop information
        lastError = error
        lastCallTime = timeSeconds

        // Return output
        return feedback + feedfrwd
    }
}
