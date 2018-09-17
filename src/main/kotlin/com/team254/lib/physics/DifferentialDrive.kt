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

@Suppress("MemberVisibilityCanBePrivate", "unused")
/**
 * Dynamic model a differential drive robot.  Note: to simplify things, this math assumes the center of mass is
 * coincident with the kinematic center of rotation (e.g. midpoint of the center axle).
 */
class DifferentialDrive(
        // All units must be SI!

        // Equivalent mass when accelerating purely linearly, in kg.
        // This is "equivalent" in that it also absorbs the effects of drivetrain inertia.
        // Measure by doing drivetrain acceleration characterization in a straight line.
        private val mass: Double,
        // Equivalent moment of inertia when accelerating purely angularly, in kg*m^2.
        // This is "equivalent" in that it also absorbs the effects of drivetrain inertia.
        // Measure by doing drivetrain acceleration characterization while turning in place.
        private val moi: Double,
        // Drag torque (proportional to angular velocity) that resists turning, in N*m/rad/s
        // Empirical testing of our drivebase showed that there was an unexplained loss in torque ~proportional to angular
        // velocity, likely due to scrub of wheels.
        // NOTE: this may not be a purely linear term, and we have done limited testing, but this factor helps our model to
        // better match reality.  For future seasons, we should investigate what's going on here...
        private val angularDrag: Double,
        // Self-explanatory.  Measure by rolling the robot a known distance and counting encoder ticks.
        private val wheelRadius: Double  // m
        ,
        // "Effective" kinematic wheelbase radius.  Might be larger than theoretical to compensate for skid steer.  Measure
        // by turning the robot in place several times and figuring out what the equivalent wheelbase radius is.
        private val effectiveWheelBaseRadius: Double  // m
        ,
        // Transmissions for both sides of the drive.
        private val leftTransmission: DCMotorTransmission,
        private val rightTransmission: DCMotorTransmission) {


    // Input/demand could be either velocity or acceleration...the math is the same.
    fun solveForwardKinematics(wheelMotion: WheelState): ChassisState {
        val chassisMotion = ChassisState()
        chassisMotion.linear = wheelRadius * (wheelMotion.right + wheelMotion.left) / 2.0
        chassisMotion.angular = wheelRadius * (wheelMotion.right - wheelMotion.left) / (2.0 * effectiveWheelBaseRadius)
        return chassisMotion
    }

    // Input/output could be either velocity or acceleration...the math is the same.
    fun solveInverseKinematics(chassisMotion: ChassisState): WheelState {
        val wheelMotion = WheelState()
        wheelMotion.left = (chassisMotion.linear - effectiveWheelBaseRadius * chassisMotion.angular) / wheelRadius
        wheelMotion.right = (chassisMotion.linear + effectiveWheelBaseRadius * chassisMotion.angular) / wheelRadius
        return wheelMotion
    }

    // Solve for torques and accelerations.
    fun solveForwardDynamics(chassisVelocity: ChassisState, voltage: WheelState): DriveDynamics {
        val dynamics = DriveDynamics()
        dynamics.wheelVelocity = solveInverseKinematics(chassisVelocity)
        dynamics.chassisVelocity = chassisVelocity
        dynamics.curvature = dynamics.chassisVelocity.angular / dynamics.chassisVelocity.linear
        if (java.lang.Double.isNaN(dynamics.curvature)) dynamics.curvature = 0.0
        dynamics.voltage = voltage
        solveForwardDynamics(dynamics)
        return dynamics
    }

    fun solveForwardDynamics(wheelVelocity: WheelState, voltage: WheelState): DriveDynamics {
        val dynamics = DriveDynamics()
        dynamics.wheelVelocity = wheelVelocity
        dynamics.chassisVelocity = solveForwardKinematics(wheelVelocity)
        dynamics.curvature = dynamics.chassisVelocity.angular / dynamics.chassisVelocity.linear
        if (java.lang.Double.isNaN(dynamics.curvature)) dynamics.curvature = 0.0
        dynamics.voltage = voltage
        solveForwardDynamics(dynamics)
        return dynamics
    }

    // Assumptions about dynamics: velocities and voltages provided.
    fun solveForwardDynamics(dynamics: DriveDynamics) {
        val leftStationary = dynamics.wheelVelocity.left epsilonEquals 0.0 && Math.abs(dynamics
                .voltage.left) < leftTransmission.frictionVoltage
        val rightStationary = dynamics.wheelVelocity.right epsilonEquals 0.0 && Math.abs(dynamics
                .voltage.right) < rightTransmission.frictionVoltage
        if (leftStationary && rightStationary) {
            // Neither side breaks static friction, so we remain stationary.
            dynamics.wheelTorque.right = 0.0
            dynamics.wheelTorque.left = dynamics.wheelTorque.right
            dynamics.chassisAcceleration.angular = 0.0
            dynamics.chassisAcceleration.linear = dynamics.chassisAcceleration.angular
            dynamics.wheelAcceleration.right = 0.0
            dynamics.wheelAcceleration.left = dynamics.wheelAcceleration.right
            dynamics.dcurvature = 0.0
            return
        }

        // Solve for motor torques generated on each side.
        dynamics.wheelTorque.left = leftTransmission.getTorqueForVoltage(dynamics.wheelVelocity.left, dynamics
                .voltage.left)
        dynamics.wheelTorque.right = rightTransmission.getTorqueForVoltage(dynamics.wheelVelocity.right, dynamics
                .voltage.right)

        // Add forces and torques about the center of mass.
        dynamics.chassisAcceleration.linear = (dynamics.wheelTorque.right + dynamics.wheelTorque.left) / (wheelRadius * mass)
        // (Tr - Tl) / r_w * r_wb - drag * w = I * angular_accel
        dynamics.chassisAcceleration.angular = effectiveWheelBaseRadius * (dynamics.wheelTorque.right - dynamics
                .wheelTorque.left) / (wheelRadius * moi) - dynamics.chassisVelocity.angular * angularDrag / moi

        // Solve for change in curvature from angular acceleration.
        // total angular accel = linear_accel * curvature + v^2 * dcurvature
        dynamics.dcurvature = (dynamics.chassisAcceleration.angular - dynamics.chassisAcceleration.linear * dynamics.curvature) / (dynamics.chassisVelocity.linear * dynamics.chassisVelocity.linear)
        if (java.lang.Double.isNaN(dynamics.dcurvature)) dynamics.dcurvature = 0.0

        // Resolve chassis accelerations to each wheel.
        dynamics.wheelAcceleration.left = dynamics.chassisAcceleration.linear - dynamics.chassisAcceleration
                .angular * effectiveWheelBaseRadius
        dynamics.wheelAcceleration.right = dynamics.chassisAcceleration.linear + dynamics.chassisAcceleration
                .angular * effectiveWheelBaseRadius
    }

    // Solve for torques and voltages.
    fun solveInverseDynamics(chassis_velocity: ChassisState, chassis_acceleration: ChassisState): DriveDynamics {
        val dynamics = DriveDynamics()
        dynamics.chassisVelocity = chassis_velocity
        dynamics.curvature = dynamics.chassisVelocity.angular / dynamics.chassisVelocity.linear
        if (java.lang.Double.isNaN(dynamics.curvature)) dynamics.curvature = 0.0
        dynamics.chassisAcceleration = chassis_acceleration
        dynamics.dcurvature = (dynamics.chassisAcceleration.angular - dynamics.chassisAcceleration.linear * dynamics.curvature) / (dynamics.chassisVelocity.linear * dynamics.chassisVelocity.linear)
        if (java.lang.Double.isNaN(dynamics.dcurvature)) dynamics.dcurvature = 0.0
        dynamics.wheelVelocity = solveInverseKinematics(chassis_velocity)
        dynamics.wheelAcceleration = solveInverseKinematics(chassis_acceleration)
        solveInverseDynamics(dynamics)
        return dynamics
    }

    fun solveInverseDynamics(wheel_velocity: WheelState, wheel_acceleration: WheelState): DriveDynamics {
        val dynamics = DriveDynamics()
        dynamics.chassisVelocity = solveForwardKinematics(wheel_velocity)
        dynamics.curvature = dynamics.chassisVelocity.angular / dynamics.chassisVelocity.linear
        if (java.lang.Double.isNaN(dynamics.curvature)) dynamics.curvature = 0.0
        dynamics.chassisAcceleration = solveForwardKinematics(wheel_acceleration)
        dynamics.dcurvature = (dynamics.chassisAcceleration.angular - dynamics.chassisAcceleration.linear * dynamics.curvature) / (dynamics.chassisVelocity.linear * dynamics.chassisVelocity.linear)
        if (java.lang.Double.isNaN(dynamics.dcurvature)) dynamics.dcurvature = 0.0
        dynamics.wheelVelocity = wheel_velocity
        dynamics.wheelAcceleration = wheel_acceleration
        solveInverseDynamics(dynamics)
        return dynamics
    }

    // Assumptions about dynamics: velocities and accelerations provided, curvature and dcurvature computed.
    fun solveInverseDynamics(dynamics: DriveDynamics) {
        // Determine the necessary torques on the left and right wheels to produce the desired wheel accelerations.
        dynamics.wheelTorque.left = wheelRadius / 2.0 * (dynamics.chassisAcceleration.linear * mass -
                dynamics.chassisAcceleration.angular * moi / effectiveWheelBaseRadius -
                dynamics.chassisVelocity.angular * angularDrag / effectiveWheelBaseRadius)
        dynamics.wheelTorque.right = wheelRadius / 2.0 * (dynamics.chassisAcceleration.linear * mass +
                dynamics.chassisAcceleration.angular * moi / effectiveWheelBaseRadius +
                dynamics.chassisVelocity.angular * angularDrag / effectiveWheelBaseRadius)

        // Solve for input voltages.
        dynamics.voltage.left = leftTransmission.getVoltageForTorque(dynamics.wheelVelocity.left, dynamics
                .wheelTorque.left)
        dynamics.voltage.right = rightTransmission.getVoltageForTorque(dynamics.wheelVelocity.right, dynamics
                .wheelTorque.right)
    }

    fun getMaxAbsVelocity(curvature: Double, /*double dcurvature, */maxAbsVoltage: Double): Double {
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

        val rightSpeedIfLeftMax = leftSpeedAtMaxVoltage * (effectiveWheelBaseRadius * curvature + 1.0) / (1.0 - effectiveWheelBaseRadius * curvature)
        if (Math.abs(rightSpeedIfLeftMax) <= rightSpeedAtMaxVoltage + kEpsilon) {
            // Left max is active constraint.
            return wheelRadius * (leftSpeedAtMaxVoltage + rightSpeedIfLeftMax) / 2.0
        }
        val leftSpeedIfRightMax = rightSpeedAtMaxVoltage * (1.0 - effectiveWheelBaseRadius * curvature) / (1.0 + effectiveWheelBaseRadius * curvature)
        // Right at max is active constraint.
        return wheelRadius * (rightSpeedAtMaxVoltage + leftSpeedIfRightMax) / 2.0
    }

    class MinMax {
        var min: Double = 0.toDouble()
        var max: Double = 0.toDouble()
    }

    // Curvature is redundant here in the case that chassisVelocity is not purely angular.  It is the responsibility of
    // the caller to ensure that curvature = angular vel / linear vel in these cases.
    fun getMinMaxAcceleration(chassisVelocity: ChassisState, curvature: Double, /*double dcurvature,*/ maxAbsVoltage: Double): MinMax {
        val result = MinMax()
        val wheelVelocities = solveInverseKinematics(chassisVelocity)
        result.min = java.lang.Double.POSITIVE_INFINITY
        result.max = java.lang.Double.NEGATIVE_INFINITY

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
                    (/*-moi * chassisVelocity.linear * chassisVelocity.linear * dcurvature*/ -dragTorque * mass * wheelRadius + fixedTorque * (linearTerm + angularTerm)) / (linearTerm - angularTerm)
                } else {
                    (/*moi * chassisVelocity.linear * chassisVelocity.linear * dcurvature*/ +dragTorque * mass * wheelRadius + fixedTorque * (linearTerm - angularTerm)) / (linearTerm + angularTerm)
                }
                val variableVoltage = variableTransmission.getVoltageForTorque(wheelVelocities[!left], variableTorque)
                if (Math.abs(variableVoltage) <= maxAbsVoltage + kEpsilon) {
                    val accel = if (java.lang.Double.isInfinite(curvature)) {
                        (if (left) -1.0 else 1.0) * (fixedTorque - variableTorque) * effectiveWheelBaseRadius / (moi * wheelRadius) - dragTorque / moi /*- chassisVelocity.linear * chassisVelocity.linear * dcurvature*/
                    } else {
                        (fixedTorque + variableTorque) / (mass * wheelRadius)
                    }
                    result.min = Math.min(result.min, accel)
                    result.max = Math.max(result.max, accel)
                }
            }
        }
        return result
    }

    // Can refer to velocity or acceleration depending on context.
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
    }

    // Can refer to velocity, acceleration, torque, voltage, etc., depending on context.
    class WheelState {
        var left: Double = 0.toDouble()
        var right: Double = 0.toDouble()

        constructor(left: Double, right: Double) {
            this.left = left
            this.right = right
        }

        constructor()

        operator fun get(get_left: Boolean): Double {
            return if (get_left) left else right
        }

        operator fun set(set_left: Boolean, `val`: Double) {
            if (set_left) {
                left = `val`
            } else {
                right = `val`
            }
        }

        override fun toString(): String {
            val fmt = DecimalFormat("#0.000")
            return fmt.format(left) + ", " + fmt.format(right)
        }
    }

    // Full state dynamics of the drivetrain.
    // TODO maybe make these all optional fields and have a single solveDynamics() method that fills in the blanks?
    class DriveDynamics : CSVWritable {
        var curvature = 0.0  // m^-1
        var dcurvature = 0.0  // m^-1/m
        var chassisVelocity = ChassisState()  // m/s
        var chassisAcceleration = ChassisState()  // m/s^2
        var wheelVelocity = WheelState()  // rad/s
        var wheelAcceleration = WheelState()  // rad/s^2
        var voltage = WheelState()  // V
        var wheelTorque = WheelState()  // N m

        override fun toCSV(): String {
            return (curvature.toString() + "," + dcurvature + "," + chassisVelocity + ", " + chassisAcceleration + ", " + wheelVelocity + ", " + wheelAcceleration
                    + ", " + voltage + ", " + wheelTorque)
        }
    }
}
