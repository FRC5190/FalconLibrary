/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Twist2d
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.mathematics.units.Rotation2d
import org.ghrobotics.lib.mathematics.units.degree
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.utils.launchFrequency

class TankDriveLocalization(
    val driveSubsystem: TankDriveSubsystem
) {

    private val localizationScope = CoroutineScope(newSingleThreadContext("Localization"))
    private val localizationMutex = Mutex()

    var robotPosition = Pose2d()
        private set

    private var prevL = 0.meter
    private var prevR = 0.meter
    private var prevA = 0.degree

    init {
        runBlocking { reset() }
        localizationScope.launchFrequency(100) { run() }
    }

    suspend fun reset(pose: Pose2d = Pose2d()) = localizationMutex.withLock {
        robotPosition = pose
        prevL = driveSubsystem.leftMaster.sensorPosition
        prevR = driveSubsystem.rightMaster.sensorPosition
        prevA = driveSubsystem.ahrsSensor.correctedAngle
    }

    private suspend fun run() = localizationMutex.withLock {
        val posL = driveSubsystem.leftMaster.sensorPosition
        val posR = driveSubsystem.rightMaster.sensorPosition

        val angA = driveSubsystem.ahrsSensor.correctedAngle

        val deltaL = posL - prevL
        val deltaR = posR - prevR
        val deltaA = angA - prevA

        robotPosition += forwardKinematics(deltaL, deltaR, deltaA).asPose

        prevL = posL
        prevR = posR
        prevA = angA
    }

    private fun forwardKinematics(leftDelta: Length, rightDelta: Length, rotationDelta: Rotation2d): Twist2d {
        val dx = (leftDelta + rightDelta) / 2.0
        return Twist2d(dx, 0.meter, rotationDelta)
    }
}
