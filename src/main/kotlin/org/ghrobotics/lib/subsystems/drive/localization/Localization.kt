package org.ghrobotics.lib.subsystems.drive.localization

import edu.wpi.first.wpilibj.Timer
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.withContext
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.launchFrequency
import java.util.concurrent.Executors

abstract class Localization(
    val robotHeading: Source<Rotation2d>
) : Source<Pose2d> {

    private val localizationContext = Executors.newSingleThreadExecutor {
        Thread(it, "Localization")
    }.asCoroutineDispatcher()

    /**
     * The robot position relative to the field.
     */
    private var robotPosition = Pose2d()

    /**
     * Stores the previous 100 states so that we can interpolate if needed.
     * Especially useful for Vision
     */
    private val interpolatableLocalizationBuffer = TimeInterpolatableBuffer<Pose2d>()

    /**
     * Stores the previous state of the robot.
     */
    private var prevHeading = Rotation2d(0.0)
    private var headingOffset = Rotation2d(0.0)

    suspend fun reset(newPosition: Pose2d = Pose2d()) =
        withContext(localizationContext) {
            resetInternal(newPosition)
        }

    protected open fun resetInternal(newPosition: Pose2d) {
        robotPosition = newPosition
        val newHeading = robotHeading()
        prevHeading = newHeading
        headingOffset = -newHeading
        interpolatableLocalizationBuffer.clear()
    }

    internal fun start() {
        GlobalScope.launchFrequency(100, localizationContext) {
            val newHeading = robotHeading()

            val deltaHeading = newHeading - prevHeading

            // Add the recorded motion of the robot during this iteration to the global robot pose.
            val newRobotPosition = robotPosition + update(deltaHeading)
            robotPosition = Pose2d(
                newRobotPosition.translation,
                newHeading + headingOffset
            )

            // Report new position to Live Dashboard
            LiveDashboard.robotHeading = robotPosition.rotation.radian
            LiveDashboard.robotX = robotPosition.translation.x.feet
            LiveDashboard.robotY = robotPosition.translation.y.feet

            prevHeading = newHeading

            // Add the global robot pose to the interpolatable buffer
            interpolatableLocalizationBuffer[Timer.getFPGATimestamp()] = robotPosition
        }
    }

    protected abstract fun update(deltaHeading: Rotation2d): Pose2d

    override fun invoke() = robotPosition

    operator fun get(timestamp: Time) = get(timestamp.second)
    internal operator fun get(timestamp: Double) = interpolatableLocalizationBuffer[timestamp]
}