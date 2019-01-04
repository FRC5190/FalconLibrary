package org.ghrobotics.lib.localization

import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.launchFrequency
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.CoroutineContext

abstract class Localization(
    val robotHeading: Source<Rotation2d>,
    context: CoroutineContext
) : Source<Pose2d> {

    private val job = Job(context[Job])
    private val scope = CoroutineScope(context + job + CoroutineName("Localization"))

    private val resetChannel = Channel<Pose2d>(Channel.CONFLATED)
    private val running = AtomicBoolean(false)

    /**
     * The robot position relative to the field.
     */
    private var robotPosition = Pose2d()

    /**
     * Stores the previous 100 states so that we can interpolate if needed.
     * Especially useful for Vision
     */
    private val interpolatableLocalizationBuffer = TimeInterpolatableBuffer<Pose2d>()

    init {
        interpolatableLocalizationBuffer.set(0.0, Pose2d())
    }

    /**
     * Stores the previous state of the robot.
     */
    private var prevHeading = Rotation2d(0.0)
    private var headingOffset = Rotation2d(0.0)

    suspend fun reset(newPosition: Pose2d = Pose2d()) = resetChannel.send(newPosition)

    protected open fun resetInternal(newPosition: Pose2d) {
        robotPosition = newPosition
        val newHeading = robotHeading()
        prevHeading = newHeading
        headingOffset = -newHeading + newPosition.rotation
        interpolatableLocalizationBuffer.clear()
    }

    fun start() {
        if (!running.compareAndSet(false, true)) return
        scope.launchFrequency(100) {
            val resetPose = resetChannel.poll()
            if (resetPose != null) {
                resetInternal(resetPose)
            }
            val newHeading = robotHeading()

            val deltaHeading = newHeading - prevHeading

            // Add the recorded motion of the robot during this iteration to the global robot pose.
            val newRobotPosition = robotPosition + update(deltaHeading)
            robotPosition = Pose2d(
                newRobotPosition.translation,
                newHeading + headingOffset
            )

            prevHeading = newHeading

            // Add the global robot pose to the interpolatable buffer
            interpolatableLocalizationBuffer[Timer.getFPGATimestamp()] = robotPosition
        }
    }

    protected abstract fun update(deltaHeading: Rotation2d): Pose2d

    override fun invoke() = robotPosition

    operator fun get(timestamp: Time) = get(timestamp.second)
    internal operator fun get(timestamp: Double) = interpolatableLocalizationBuffer[timestamp]

    open fun dispose() {
        job.cancel()
        resetChannel.close()
    }
}