package org.ghrobotics.lib.subsystems.drive.localization

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.withContext
import org.ghrobotics.lib.debug.LiveDashboard
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.launchFrequency

abstract class Localization(
    val robotHeading: Source<Rotation2d>
) {

    @ObsoleteCoroutinesApi
    private val localizationContext = newSingleThreadContext("Localization")

    /**
     * The robot position relative to the field.
     */
    var robotPosition = Pose2d()
        private set

    /**
     * Stores the previous state of the robot.
     */
    private var prevHeading = Rotation2d(0.0)

    @ObsoleteCoroutinesApi
    suspend fun reset(newPosition: Pose2d = Pose2d()) =
        withContext(localizationContext) {
            resetInternal(newPosition)
        }

    protected open fun resetInternal(newPosition: Pose2d) {
        robotPosition = newPosition
        prevHeading = robotHeading()
    }

    @ObsoleteCoroutinesApi
    internal fun start() {
        GlobalScope.launchFrequency(100, localizationContext) {
            val newHeading = robotHeading()

            val deltaHeading = newHeading - prevHeading

            // Add the recorded motion of the robot during this iteration to the global robot pose.
            robotPosition += update(deltaHeading)

            // Report new position to Live Dashboard
            LiveDashboard.robotHeading = newHeading.radian
            LiveDashboard.robotX = robotPosition.translation.x.feet
            LiveDashboard.robotY = robotPosition.translation.y.feet

            prevHeading = newHeading
        }
    }

    protected abstract fun update(deltaHeading: Rotation2d): Pose2d

}