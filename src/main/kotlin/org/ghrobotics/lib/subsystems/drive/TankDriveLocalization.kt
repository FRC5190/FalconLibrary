/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.utils.launchFrequency

/**
 * Odometry for a standard tank drive drivetrain.
 */
class TankDriveLocalization {

    private lateinit var driveSubsystem: TankDriveSubsystem

    private val localizationScope = CoroutineScope(newSingleThreadContext("Localization"))
    private val localizationMutex = Mutex()

    /**
     * The robot position relative to the field.
     */
    var robotPosition = Pose2d()
        private set

    /**
     * Stores the previous left, right, and angle configurations of the robot.
     */
    private var prevL = 0.0
    private var prevR = 0.0
    private var prevA = 0.degree

    /**
     * Initialize odometry with the specified drive subsystem.
     */
    internal suspend fun lateInit(driveSubsystem: TankDriveSubsystem) {
        this.driveSubsystem = driveSubsystem
        reset()
        localizationScope.launchFrequency(100) { run() }
    }

    /**
     * Reset odometry with a new position.
     */
    suspend fun reset(pose: Pose2d = Pose2d()) = localizationMutex.withLock {
        robotPosition = pose
        prevL = driveSubsystem.leftMaster.sensorPosition.value
        prevR = driveSubsystem.rightMaster.sensorPosition.value
        prevA = driveSubsystem.ahrsSensor.correctedAngle
    }

    /**
     * Update the global robot position with new data.
     */
    private suspend fun run() = localizationMutex.withLock {
        val posL = driveSubsystem.leftMaster.sensorPosition.value
        val posR = driveSubsystem.rightMaster.sensorPosition.value

        val angA = driveSubsystem.ahrsSensor.correctedAngle

        val deltaL = posL - prevL
        val deltaR = posR - prevR
        val deltaA = angA - prevA

        // Add the recorded motion of the robot during this iteration to the global robot pose.
        robotPosition += forwardKinematics(deltaL, deltaR, deltaA).asPose

        prevL = posL
        prevR = posR
        prevA = angA
    }

    /**
     * Return a twist that represents the robot's motion from the left delta, the right delta, and the rotation delta.
     */
    private fun forwardKinematics(leftDelta: Double, rightDelta: Double, rotationDelta: Rotation2d): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx, 0.0, rotationDelta)
    }
}
