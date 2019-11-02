package org.ghrobotics.lib.mathematics.twodim.trajectory.constraints

import edu.wpi.first.wpilibj.geometry.Pose2d
import edu.wpi.first.wpilibj.kinematics.ChassisSpeeds
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveKinematics
import edu.wpi.first.wpilibj.kinematics.DifferentialDriveWheelSpeeds
import edu.wpi.first.wpilibj.trajectory.constraint.TrajectoryConstraint
import org.ejml.simple.SimpleMatrix
import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.inMeters
import org.ghrobotics.lib.physics.MotorCharacterization
import kotlin.math.*

@Suppress("unused")
class DifferentailDriveDynamicsConstraint(
    private val leftMotor: MotorCharacterization<Meter>,
    private val rightMotor: MotorCharacterization<Meter>,
    private val kinematics: DifferentialDriveKinematics,
    private val maxVoltage: SIUnit<Volt>,
    private val wheelBase: SIUnit<Meter>
): TrajectoryConstraint {

    val A = SimpleMatrix(arrayOf(
        doubleArrayOf(-((leftMotor.kV + rightMotor.kV).value / 2.0) / ((leftMotor.kA + rightMotor.kA).value / 2.0), 0.0),
        doubleArrayOf(0.0, -((leftMotor.kV + rightMotor.kV).value / 2.0) / ((leftMotor.kA + rightMotor.kA).value / 2.0))
    ))

    val B = SimpleMatrix(arrayOf(
        doubleArrayOf(1.0 / ((leftMotor.kA + rightMotor.kA).value / 2.0), 0.0),
        doubleArrayOf(0.0, (1.0 / ((leftMotor.kA + rightMotor.kA).value / 2.0)))
    ))

    private val maxKs = max(leftMotor.kS.value, rightMotor.kS.value)
    private val uMin = SimpleMatrix(arrayOf(doubleArrayOf(-maxVoltage.value + maxKs), doubleArrayOf(-maxVoltage.value + maxKs)))
    private val uMax = SimpleMatrix(arrayOf(doubleArrayOf(maxVoltage.value - maxKs), doubleArrayOf(maxVoltage.value - maxKs)))

    override fun getMinMaxAccelerationMetersPerSecondSq(poseMeters: Pose2d, curvatureRadPerMeter: Double,
                                                        velocityMetersPerSecond: Double): TrajectoryConstraint.MinMax {
        // left and right vel
        val wheelSpeeds = kinematics.toWheelSpeeds(ChassisSpeeds(
                velocityMetersPerSecond,
                0.0,
                curvatureRadPerMeter * velocityMetersPerSecond))

        val x = SimpleMatrix(arrayOf(
            doubleArrayOf(wheelSpeeds.leftMetersPerSecond),
            doubleArrayOf(wheelSpeeds.rightMetersPerSecond
        )))

        val xdotMax = A.mult(x).plus(B.dot(uMax))
        val xdotMin = A.mult(x).plus(B.dot(uMin))

        // go back from wheel speeds to chassis speeds
        val minChassisSpeed = kinematics.toChassisSpeeds(DifferentialDriveWheelSpeeds(
            xdotMin[0], xdotMin[1]
        ))
        val maxChassisSpeed = kinematics.toChassisSpeeds(DifferentialDriveWheelSpeeds(
            xdotMax[0], xdotMax[1]
        ))

        return TrajectoryConstraint.MinMax(
            minChassisSpeed.vxMetersPerSecond, maxChassisSpeed.vxMetersPerSecond
        )
    }


    override fun getMaxVelocityMetersPerSecond(poseMeters: Pose2d, curvatureRadPerMeter: Double, velocityMetersPerSecond: Double): Double {
        val leftMax = leftMotor.kV.value * maxVoltage.value
        val rightMax = rightMotor.kV.value * maxVoltage.value
        val effectiveWheelBaseRadius = wheelBase.inMeters()

        if(curvatureRadPerMeter epsilonEquals 0.0) return min(leftMax, rightMax)
        if(java.lang.Double.isInfinite(curvatureRadPerMeter))
            return sign(curvatureRadPerMeter) * min(leftMax, rightMax) /effectiveWheelBaseRadius

        val rightSpeedIfLeftMax =
            leftMax * (effectiveWheelBaseRadius * curvatureRadPerMeter + 1.0) / (1.0 - effectiveWheelBaseRadius * curvatureRadPerMeter)

        if (abs(rightSpeedIfLeftMax) <= rightMax + kEpsilon)
            // Left max is active constraint.
            return (leftMax + rightSpeedIfLeftMax) / 2.0

        val leftSpeedIfRightMax =
            rightMax * (1.0 - effectiveWheelBaseRadius * curvatureRadPerMeter) / (1.0 + effectiveWheelBaseRadius * curvatureRadPerMeter)

        // Right at max is active constraint.
        return (rightMax + leftSpeedIfRightMax) / 2.0
    }
}