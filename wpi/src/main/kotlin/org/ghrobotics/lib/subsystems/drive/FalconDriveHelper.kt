/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive

import org.ghrobotics.lib.mathematics.max
import org.ghrobotics.lib.mathematics.twodim.geometry.Rotation2d
import org.ghrobotics.lib.mathematics.twodim.geometry.Translation2d
import org.ghrobotics.lib.subsystems.drive.utils.DriveSignal
import org.ghrobotics.lib.subsystems.drive.utils.Kinematics
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sign
import kotlin.math.withSign

/**
 * Helper class that contains all types of driving -- tank drive,
 * curvature drive, arcade drive.
 */
class FalconDriveHelper {
    private var quickStopAccumulator = 0.0

    /**
     * Drives the robot using arcade drive.
     *
     * @param linearPercent The percent for linear motion.
     * @param rotationPercent The percent for angular rotation.
     */
    fun arcadeDrive(
        linearPercent: Double,
        rotationPercent: Double,
    ): Pair<Double, Double> {
        val maxInput = max(linearPercent.absoluteValue, rotationPercent.absoluteValue)
            .withSign(linearPercent)

        val leftMotorOutput: Double
        val rightMotorOutput: Double

        if (linearPercent >= 0.0) {
            // First quadrant, else second quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            } else {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            }
        } else {
            // Third quadrant, else fourth quadrant
            if (rotationPercent >= 0.0) {
                leftMotorOutput = linearPercent + rotationPercent
                rightMotorOutput = maxInput
            } else {
                leftMotorOutput = maxInput
                rightMotorOutput = linearPercent - rotationPercent
            }
        }

        return Pair(leftMotorOutput, rightMotorOutput)
    }

    /**
     * Drives the robot using curvature drive.
     *
     * @param linearPercent The percent for linear motion.
     * @param curvaturePercent The percent for curvature of the robot.
     * @param isQuickTurn Whether to use arcade drive or not.
     */
    fun curvatureDrive(
        linearPercent: Double,
        curvaturePercent: Double,
        isQuickTurn: Boolean,
    ): Pair<Double, Double> {
        val angularPower: Double
        val overPower: Boolean

        if (isQuickTurn) {
            if (linearPercent.absoluteValue < kQuickStopThreshold) {
                quickStopAccumulator = (1 - kQuickStopAlpha) * quickStopAccumulator +
                    kQuickStopAlpha * curvaturePercent.coerceIn(-1.0, 1.0) * 2.0
            }
            overPower = true
            angularPower = curvaturePercent
        } else {
            overPower = false
            angularPower = linearPercent.absoluteValue * curvaturePercent - quickStopAccumulator

            when {
                quickStopAccumulator > 1 -> quickStopAccumulator -= 1.0
                quickStopAccumulator < -1 -> quickStopAccumulator += 1.0
                else -> quickStopAccumulator = 0.0
            }
        }

        var leftMotorOutput = linearPercent + angularPower
        var rightMotorOutput = linearPercent - angularPower

        // If rotation is overpowered, reduce both outputs to within acceptable range
        if (overPower) {
            when {
                leftMotorOutput > 1.0 -> {
                    rightMotorOutput -= leftMotorOutput - 1.0
                    leftMotorOutput = 1.0
                }
                rightMotorOutput > 1.0 -> {
                    leftMotorOutput -= rightMotorOutput - 1.0
                    rightMotorOutput = 1.0
                }
                leftMotorOutput < -1.0 -> {
                    rightMotorOutput -= leftMotorOutput + 1.0
                    leftMotorOutput = -1.0
                }
                rightMotorOutput < -1.0 -> {
                    leftMotorOutput -= rightMotorOutput + 1.0
                    rightMotorOutput = -1.0
                }
            }
        }

        // Normalize the wheel speeds
        val maxMagnitude = max(leftMotorOutput.absoluteValue, rightMotorOutput.absoluteValue)
        if (maxMagnitude > 1.0) {
            leftMotorOutput /= maxMagnitude
            rightMotorOutput /= maxMagnitude
        }

        return Pair(leftMotorOutput, rightMotorOutput)
    }

    fun swerveDrive(
        drivetrain: FalconSwerveDrivetrain,
        forwardInput: Double,
        strafeInput: Double,
        rotationInput: Double,
        fieldRelative: Boolean,
    ): DriveSignal {
        var translationalInput = Translation2d(forwardInput, strafeInput)
        var inputMagnitude: Double = translationalInput.norm()
        var rotationInput = rotationInput

        // Snap the translational input to its nearest pole, if it is within a certain
        // threshold of it.
        if (fieldRelative) {
            if (abs(
                    translationalInput.direction()
                        .distance(translationalInput.direction().nearestPole()),
                ) < kPoleThreshold
            ) {
                translationalInput = translationalInput.direction().nearestPole().toTranslation().scale(inputMagnitude)
            }
        } else {
            if (abs(
                    translationalInput.direction()
                        .distance(translationalInput.direction().nearestPole()),
                ) < kRobotRelativePoleThreshold
            ) {
                translationalInput = translationalInput.direction().nearestPole().toTranslation().scale(inputMagnitude)
            }
        }

        if (inputMagnitude < kDeadband) {
            translationalInput = Translation2d()
            inputMagnitude = 0.0
        }

        // Scale x and y by applying a power to the magnitude of the vector they create,
        // in order to make the controls less sensitive at the lower end.

        // Scale x and y by applying a power to the magnitude of the vector they create,
        // in order to make the controls less sensitive at the lower end.
        val power: Double = kLowAdjustmentPower
        val direction: Rotation2d = translationalInput.direction()
        val scaledMagnitude = inputMagnitude.pow(power)
        translationalInput = Translation2d(direction.cos() * scaledMagnitude, direction.sin() * scaledMagnitude)

        rotationInput = if (abs(rotationInput) < kRotationDeadband) 0.0 else rotationInput
        rotationInput = abs(rotationInput).pow(kRotationExponent) * sign(rotationInput)

        translationalInput = translationalInput.scale(kMaxSpeed)
        rotationInput *= kMaxSpeed
        rotationInput *= kHighPowerRotationScalar

        return Kinematics(drivetrain).inverseKinematics(
            translationalInput.x(),
            translationalInput.y(),
            rotationInput,
            fieldRelative,
        )
    }

    companion object {
        const val kQuickStopThreshold = 0.2
        const val kQuickStopAlpha = 0.1
        private const val kHighAdjustmentPower = 1.75 + 0.4375
        private const val kLowAdjustmentPower = 1.50
        private const val kMaxSpeed = 1.0
        private const val kHighPowerRotationScalar = 0.8
        private const val kLowPowerScalar = 0.5
        private const val kRotationExponent = 4.0
        private const val kPoleThreshold = 0.0
        private val kRobotRelativePoleThreshold = Math.toRadians(5.0)
        private const val kDeadband = 0.25
        private const val kRotationDeadband = 0.15
    }
}
