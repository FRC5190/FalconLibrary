package org.ghrobotics.lib.mathematics.twodim.trajectory

import com.team254.lib.physics.DCMotorTransmission
import com.team254.lib.physics.DifferentialDrive
import koma.platformsupport.assert
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.twodim.geometry.degrees
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.CentripetalAccelerationConstraint
import org.ghrobotics.lib.mathematics.twodim.trajectory.constraints.DifferentialDriveDynamicsConstraint
import org.ghrobotics.lib.mathematics.units.derivedunits.acceleration
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import kotlin.math.absoluteValue
import kotlin.math.pow

class TrajectoryGeneratorTest {

    companion object {
        private const val kRobotLinearInertia = 60.0  // kg
        private const val kRobotAngularInertia = 10.0  // kg m^2
        private const val kRobotAngularDrag = 12.0  // N*m / (rad/sec)
        private const val kDriveVIntercept = 1.055  // V
        private const val kDriveKv = 0.135  // V per rad/s
        private const val kDriveKa = 0.012  // V per rad/s^2

        private val kDriveWheelRadiusInches = 3.0.inch
        private val kWheelBaseDiameter = 29.5.inch

        private val transmission = DCMotorTransmission(
                speedPerVolt = 1 / kDriveKv,
                torquePerVolt = kDriveWheelRadiusInches.asMetric.asDouble.pow(2) * kRobotLinearInertia / (2.0 * kDriveKa),
                frictionVoltage = kDriveVIntercept
        )

        val drive = DifferentialDrive(
                mass = kRobotLinearInertia,
                moi = kRobotAngularInertia,
                angularDrag = kRobotAngularDrag,
                wheelRadius = kDriveWheelRadiusInches.asMetric.asDouble,
                effectiveWheelBaseRadius = kWheelBaseDiameter.asMetric.asDouble / 2.0,
                leftTransmission = transmission,
                rightTransmission = transmission
        )

        private val kMaxCentripetalAcceleration = 8.feet.acceleration
        private val kMaxAcceleration = 8.feet.acceleration
        private val kMaxVelocity = 10.feet.velocity

        private const val kTolerance = 0.1

        private val kSideStart = Pose2d(1.54.feet, 23.234167.feet, 180.degrees)
        private val kNearScaleEmpty = Pose2d(23.7.feet, 20.2.feet, 160.degrees)

        val trajectory = DefaultTrajectoryGenerator.generateTrajectory(
                listOf(
                        kSideStart,
                        kSideStart + Pose2d((-13).feet, 0.feet, 0.degrees),
                        kSideStart + Pose2d((-19.5).feet, 5.feet, (-90).degrees),
                        kSideStart + Pose2d((-19.5).feet, 14.feet, (-90).degrees),
                        kNearScaleEmpty.mirror
                ),
                listOf(
                        CentripetalAccelerationConstraint(kMaxCentripetalAcceleration),
                        DifferentialDriveDynamicsConstraint(drive, 9.0)),
                0.0.feet.velocity,
                0.0.feet.velocity,
                kMaxVelocity,
                kMaxAcceleration,
                true
        )
    }

    @Test
    fun testTrajectoryGenerationAndConstraints() {
        val iterator = trajectory.iterator()

        var time = 0.second
        val dt = 20.millisecond

        while (!iterator.isDone) {
            val pt = iterator.advance(dt)
            time += dt

            val ac = pt.state.velocity.pow(2) * pt.state.state.curvature.curvature

            assert(ac <= kMaxCentripetalAcceleration.asMetric.asDouble + kTolerance)
            assert(pt.state.velocity.absoluteValue < kMaxVelocity.asMetric.asDouble + kTolerance)
            assert(pt.state.acceleration.absoluteValue < kMaxAcceleration.asMetric.asDouble + kTolerance)
        }
    }
}