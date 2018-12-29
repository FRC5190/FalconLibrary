@file:Suppress("MemberVisibilityCanBePrivate", "unused")

package com.team254.lib.physics

import org.ghrobotics.lib.mathematics.epsilonEquals
import org.ghrobotics.lib.mathematics.kEpsilon
import org.ghrobotics.lib.types.CSVWritable
import java.text.DecimalFormat
import java.util.*

/*
 * Implementation from:
 * NASA Ames Robotics "The Cheesy Poofs"
 * Team 254
 */


/**
 * Dynamic model a differential drive robot.  Note: to simplify things, this math assumes the center of mass is
 * coincident with the kinematic center of rotation (e.g. midpoint of the center axle).
 */
@Suppress("LongParameterList")
class DifferentialDrive(
    /**
     * Equivalent mass when accelerating purely linearly, in kg.
     * This is equivalent in that it also absorbs the effects of drivetrain inertia.
     * Measure by doing drivetrain acceleration characterizaton in a straight line.
     */
    private val mass: Double,
    /**
     * Equivalent moment of inertia when accelerating purely angularly in kg m^2.
     * This is equivalent in that it also absorbs the effects of drivetrain inertia.
     * Measure by doing drivetrain acceleration characterization while turing in place.
     */
    private val moi: Double,
    /**
     * Drag torque (proportional to angular velocity) that resists turning in Nm/rad/s.
     * Empirical testing of 254's drivebase showed that there was an unexplained loss in torque ~proportional to angular
     * velocity, likely due to scrub of wheels.
     * NOTE: this may not be a purely linear tern, and 254 has done limited testing, but this factor may help a model
     * to better match reality.
     */
    private val angularDrag: Double,
    /**
     * The radius of the wheel
     */
    val wheelRadius: Double, // m,
    /**
     * Effective kinematic wheelbase radius. Might be larger than theoretical to compensate for skid steer. Measure by
     * turning the robot in place several times and figuring out what the equivalent wheel base radius is.
     */
    private val effectiveWheelBaseRadius: Double, // m
    /**
     * DC Motor transmission for both sides of the drivetrain.
     */
    private val leftTransmission: DCMotorTransmission,
    private val rightTransmission: DCMotorTransmission
) {

    /**
     * Solve forward kinematics to get chassis motion from wheel motion.
     * Could be either acceleration or velocity.
     */
    fun solveForwardKinematics(wheelMotion: WheelState): ChassisState = ChassisState(
        wheelRadius * (wheelMotion.right + wheelMotion.left) / 2.0,
        wheelRadius * (wheelMotion.right - wheelMotion.left) / (2.0 * effectiveWheelBaseRadius)
    )

    /**
     * Solve inverse kinematics to get wheel motion from chassis motion.
     * Could be either acceleration or velocity.
     */
    fun solveInverseKinematics(chassisMotion: ChassisState): WheelState = WheelState(
        (chassisMotion.linear - effectiveWheelBaseRadius * chassisMotion.angular) / wheelRadius,
        (chassisMotion.linear + effectiveWheelBaseRadius * chassisMotion.angular) / wheelRadius
    )

    /**
     * Solve forward dynamics for torques and accelerations.
     */
    fun solveForwardDynamics(chassisVelocity: ChassisState, voltage: WheelState): DriveDynamics = solveForwardDynamics(
        solveInverseKinematics(chassisVelocity),
        chassisVelocity,
        (chassisVelocity.angular / chassisVelocity.linear).let { if (it.isNaN()) 0.0 else it },
        voltage
    )

    /**
     * Get the voltage simply from the Kv and the friction voltage of the transmissions
     */
    fun getVoltagesFromkV(velocities: DifferentialDrive.WheelState): WheelState {
        return DifferentialDrive.WheelState(
            velocities.left / leftTransmission.speedPerVolt +
                leftTransmission.frictionVoltage * Math.signum(velocities.left),
            velocities.right / rightTransmission.speedPerVolt +
                rightTransmission.frictionVoltage * Math.signum(velocities.right)
        )
    }

    /**
     * Solve forward dynamics for torques and accelerations.
     */
    fun solveForwardDynamics(wheelVelocity: WheelState, voltage: WheelState): DriveDynamics {
        val chassisVelocity = solveForwardKinematics(wheelVelocity)
        return solveForwardDynamics(
            wheelVelocity,
            chassisVelocity,
            (chassisVelocity.angular / chassisVelocity.linear).let { if (it.isNaN()) 0.0 else it },
            voltage
        )
    }

    /**
     * Solve forward dynamics for torques and accelerations.
     */
    fun solveForwardDynamics(
        wheelVelocity: WheelState,
        chassisVelocity: ChassisState,
        curvature: Double,
        voltage: WheelState
    ): DriveDynamics {

        val wheelTorque: WheelState
        val chassisAcceleration: ChassisState
        val wheelAcceleration: WheelState
        val dcurvature: Double


        val leftStationary = wheelVelocity.left epsilonEquals 0.0 && Math.abs(
            voltage.left
        ) < leftTransmission.frictionVoltage
        val rightStationary = wheelVelocity.right epsilonEquals 0.0 && Math.abs(
            voltage.right
        ) < rightTransmission.frictionVoltage


        // Neither side breaks static friction, so we remain stationary.
        if (leftStationary && rightStationary) {
            wheelTorque = WheelState()
            chassisAcceleration = ChassisState()
            wheelAcceleration = WheelState()
            dcurvature = 0.0
        } else {
            // Solve for motor torques generated on each side.
            wheelTorque = WheelState(
                leftTransmission.getTorqueForVoltage(wheelVelocity.left, voltage.left),
                rightTransmission.getTorqueForVoltage(wheelVelocity.right, voltage.right)
            )

            // Add forces and torques about the center of mass.
            chassisAcceleration = ChassisState(
                (wheelTorque.right + wheelTorque.left) / (wheelRadius * mass),
                // (Tr - Tl) / r_w * r_wb - drag * w = I * angular_accel
                effectiveWheelBaseRadius * (wheelTorque.right - wheelTorque.left) /
                    (wheelRadius * moi) - chassisVelocity.angular * angularDrag / moi
            )

            // Solve for change in curvature from angular acceleration.
            // total angular accel = linear_accel * curvature + v^2 * dcurvature
            dcurvature = ((chassisAcceleration.angular - chassisAcceleration.linear * curvature) /
                (chassisVelocity.linear * chassisVelocity.linear)).let { if (it.isNaN()) 0.0 else it }

            // Resolve chassis accelerations to each wheel.
            wheelAcceleration = WheelState(
                chassisAcceleration.linear - chassisAcceleration.angular * effectiveWheelBaseRadius,
                chassisAcceleration.linear + chassisAcceleration.angular * effectiveWheelBaseRadius
            )
        }
        return DriveDynamics(
            curvature,
            dcurvature,
            chassisVelocity,
            chassisAcceleration,
            wheelVelocity,
            wheelAcceleration,
            voltage,
            wheelTorque
        )
    }


    /**
     * Solve inverse dynamics for torques and voltages
     */
    fun solveInverseDynamics(chassisVelocity: ChassisState, chassisAcceleration: ChassisState): DriveDynamics {

        val curvature = (chassisVelocity.angular / chassisVelocity.linear)
            .let { if (it.isNaN()) 0.0 else it }

        val dcurvature = ((chassisAcceleration.angular - chassisAcceleration.linear * curvature) /
            (chassisVelocity.linear * chassisVelocity.linear))
            .let { if (it.isNaN()) 0.0 else it }

        return solveInverseDynamics(
            solveInverseKinematics(chassisVelocity),
            chassisVelocity,
            solveInverseKinematics(chassisAcceleration),
            chassisAcceleration,
            curvature,
            dcurvature
        )
    }

    /**
     * Solve inverse dynamics for torques and voltages
     */
    fun solveInverseDynamics(wheelVelocity: WheelState, wheelAcceleration: WheelState): DriveDynamics {

        val chassisVelocity = solveForwardKinematics(wheelVelocity)
        val chassisAcceleration = solveForwardKinematics(wheelAcceleration)

        val curvature = (chassisVelocity.angular / chassisVelocity.linear)
            .let { if (it.isNaN()) 0.0 else it }

        val dcurvature = ((chassisAcceleration.angular - chassisAcceleration.linear * curvature) /
            (chassisVelocity.linear * chassisVelocity.linear))
            .let { if (it.isNaN()) 0.0 else it }

        return solveInverseDynamics(
            wheelVelocity,
            chassisVelocity,
            wheelAcceleration,
            chassisAcceleration,
            curvature,
            dcurvature
        )
    }


    /**
     * Solve inverse dynamics for torques and voltages
     */
    fun solveInverseDynamics(
        wheelVelocity: WheelState,
        chassisVelocity: ChassisState,
        wheelAcceleration: WheelState,
        chassisAcceleration: ChassisState,
        curvature: Double,
        dcurvature: Double
    ): DriveDynamics {


        // Determine the necessary torques on the left and right wheels to produce the desired wheel accelerations.
        val wheelTorque = WheelState(
            wheelRadius / 2.0 * (chassisAcceleration.linear * mass -
                chassisAcceleration.angular * moi / effectiveWheelBaseRadius -
                chassisVelocity.angular * angularDrag / effectiveWheelBaseRadius),

            wheelRadius / 2.0 * (chassisAcceleration.linear * mass +
                chassisAcceleration.angular * moi / effectiveWheelBaseRadius +
                chassisVelocity.angular * angularDrag / effectiveWheelBaseRadius)
        )

        // Solve for input voltages
        val voltage = WheelState(
            leftTransmission.getVoltageForTorque(wheelVelocity.left, wheelTorque.left),
            rightTransmission.getVoltageForTorque(wheelVelocity.right, wheelTorque.right)
        )

        return DriveDynamics(
            curvature,
            dcurvature,
            chassisVelocity,
            chassisAcceleration,
            wheelVelocity,
            wheelAcceleration,
            voltage,
            wheelTorque
        )
    }

    /**
     * Solve for the max absolute velocity that the drivetrain is capable of given a max voltage and curvature
     */
    fun getMaxAbsVelocity(curvature: Double, /*double dcurvature, */ maxAbsVoltage: Double): Double {
        // Alternative implementation:
        // (Tr - Tl) * r_wb / r_w = I * v^2 * dk
        // (Tr + Tl) / r_w = 0
        // T = Tr = -Tl
        // 2T * r_wb / r_w = I*v^2*dk
        // T = 2*I*v^2*dk*r_w/r_wb
        // T = kt*(-vR/kv + V) = -kt*(-vL/vmax + V)
        // Vr = v * (1 + k*r_wb)
        // 0 = 2*I*dk*r_w/r_wb * v^2 + kt * ((1 + k*r_wb) * v / kv) - kt * V
        // solve using quadratic formula?
        // -b +/- sqrt(b^2 - 4*a*c) / (2a)

        // k = w / v
        // v = r_w*(wr + wl) / 2
        // w = r_w*(wr - wl) / (2 * r_wb)
        // Plug in maxAbsVoltage for each wheel.

        val leftSpeedAtMaxVoltage = leftTransmission.getFreeSpeedAtVoltage(maxAbsVoltage)
        val rightSpeedAtMaxVoltage = rightTransmission.getFreeSpeedAtVoltage(maxAbsVoltage)

        if (curvature epsilonEquals 0.0) {
            return wheelRadius * Math.min(leftSpeedAtMaxVoltage, rightSpeedAtMaxVoltage)
        }
        if (java.lang.Double.isInfinite(curvature)) {
            // Turn in place.  Return value meaning becomes angular velocity.
            val wheelSpeed = Math.min(leftSpeedAtMaxVoltage, rightSpeedAtMaxVoltage)
            return Math.signum(curvature) * wheelRadius * wheelSpeed / effectiveWheelBaseRadius
        }

        val rightSpeedIfLeftMax =
            leftSpeedAtMaxVoltage * (effectiveWheelBaseRadius * curvature + 1.0) / (1.0 - effectiveWheelBaseRadius * curvature)

        if (Math.abs(rightSpeedIfLeftMax) <= rightSpeedAtMaxVoltage + kEpsilon) {
            // Left max is active constraint.
            return wheelRadius * (leftSpeedAtMaxVoltage + rightSpeedIfLeftMax) / 2.0
        }
        val leftSpeedIfRightMax =
            rightSpeedAtMaxVoltage * (1.0 - effectiveWheelBaseRadius * curvature) / (1.0 + effectiveWheelBaseRadius * curvature)

        // Right at max is active constraint.
        return wheelRadius * (rightSpeedAtMaxVoltage + leftSpeedIfRightMax) / 2.0
    }


    data class MinMax(val min: Double, val max: Double)

    // Curvature is redundant here in the case that chassisVelocity is not purely angular.  It is the responsibility of
    // the caller to ensure that curvature = angular vel / linear vel in these cases.
    @Suppress("ComplexMethod", "NestedBlockDepth")
    fun getMinMaxAcceleration(
        chassisVelocity: ChassisState,
        curvature: Double, /*double dcurvature,*/
        maxAbsVoltage: Double
    ): MinMax {
        val wheelVelocities = solveInverseKinematics(chassisVelocity)

        var min = java.lang.Double.POSITIVE_INFINITY
        var max = java.lang.Double.NEGATIVE_INFINITY

        // Math:
        // (Tl + Tr) / r_w = m*a
        // (Tr - Tl) / r_w * r_wb - drag*w = i*(a * k + v^2 * dk)

        // 2 equations, 2 unknowns.
        // Solve for a and (Tl|Tr)

        val linearTerm = if (java.lang.Double.isInfinite(curvature)) 0.0 else mass * effectiveWheelBaseRadius
        val angularTerm = if (java.lang.Double.isInfinite(curvature)) moi else moi * curvature

        val dragTorque = chassisVelocity.angular * angularDrag

        // Check all four cases and record the min and max valid accelerations.
        for (left in Arrays.asList(false, true)) {
            for (sign in Arrays.asList(1.0, -1.0)) {

                val fixedTransmission = if (left) leftTransmission else rightTransmission
                val variableTransmission = if (left) rightTransmission else leftTransmission
                val fixedTorque = fixedTransmission.getTorqueForVoltage(wheelVelocities[left], sign * maxAbsVoltage)
                var variableTorque: Double
                // NOTE: variable_torque is wrong.  Units don't work out correctly.  We made a math error somewhere...
                // Leaving this "as is" for code release so as not to be disingenuous, but this whole function needs
                // revisiting in the future...
                variableTorque = if (left) {
                    (/*-moi * chassisVelocity.linear * chassisVelocity.linear * dcurvature*/
                        -dragTorque * mass * wheelRadius + fixedTorque * (linearTerm + angularTerm)) /
                        (linearTerm - angularTerm)
                } else {
                    (/*moi * chassisVelocity.linear * chassisVelocity.linear * dcurvature*/
                        +dragTorque * mass * wheelRadius + fixedTorque * (linearTerm - angularTerm)) /
                        (linearTerm + angularTerm)
                }
                val variableVoltage = variableTransmission.getVoltageForTorque(wheelVelocities[!left], variableTorque)
                if (Math.abs(variableVoltage) <= maxAbsVoltage + kEpsilon) {
                    val accel = if (java.lang.Double.isInfinite(curvature)) {
                        (if (left) -1.0 else 1.0) * (fixedTorque - variableTorque) *
                            effectiveWheelBaseRadius / (moi * wheelRadius) - dragTorque / moi
                        /*- chassisVelocity.linear * chassisVelocity.linear * dcurvature*/
                    } else {
                        (fixedTorque + variableTorque) / (mass * wheelRadius)
                    }
                    min = Math.min(min, accel)
                    max = Math.max(max, accel)
                }
            }
        }
        return MinMax(min, max)
    }

    /**
     * Can refer to velocity or acceleration depending on context.
     */
    class ChassisState {
        var linear: Double = 0.toDouble()
        var angular: Double = 0.toDouble()

        constructor(linear: Double, angular: Double) {
            this.linear = linear
            this.angular = angular
        }

        constructor()

        override fun toString(): String {
            val fmt = DecimalFormat("#0.000")
            return fmt.format(linear) + ", " + fmt.format(angular)
        }

        operator fun minus(other: ChassisState) =
            ChassisState(this.linear - other.linear, this.angular - other.angular)

        operator fun times(scalar: Double) = ChassisState(linear * scalar, angular * scalar)
        operator fun div(scalar: Double) = this * (1 / scalar)
    }

    /**
     * Can refer to velocity, acceleration, torque, voltage, etc., depending on context.
     */
    class WheelState(val left: Double, val right: Double) {
        constructor() : this(0.0, 0.0)

        operator fun get(isLeft: Boolean) =  if (isLeft) left else right

        override fun toString(): String {
            val fmt = DecimalFormat("#0.000")
            return fmt.format(left) + ", " + fmt.format(right)
        }
    }

    /**
     * Full state dynamics of the drivetrain
     */
    class DriveDynamics(
        val curvature: Double, // m^-1
        val dcurvature: Double, // m^-2
        val chassisVelocity: ChassisState, // m/s
        val chassisAcceleration: ChassisState, // m/s^2
        val wheelVelocity: WheelState, // rad/s
        val wheelAcceleration: WheelState, // rad/s^2
        val voltage: WheelState, // V
        val wheelTorque: WheelState // N m
    ) : CSVWritable {
        override fun toCSV(): String {
            return "$curvature,$dcurvature,$chassisVelocity, $chassisAcceleration, " +
                "$wheelVelocity, $wheelAcceleration, $voltage, $wheelTorque"
        }
    }
}
