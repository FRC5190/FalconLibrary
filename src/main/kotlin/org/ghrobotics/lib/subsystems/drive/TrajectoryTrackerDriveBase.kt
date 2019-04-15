package org.ghrobotics.lib.subsystems.drive

import com.team254.lib.physics.DifferentialDrive
import org.ghrobotics.lib.mathematics.twodim.control.TrajectoryTracker
import org.ghrobotics.lib.mathematics.twodim.geometry.Pose2d
import org.ghrobotics.lib.mathematics.units.derivedunits.*
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.radian
import org.ghrobotics.lib.motors.LinearFalconMotor

/**
 * Just implement this if you want to use the TrajectoryTrackerCommand.
 */
interface TrajectoryTrackerDriveBase {
    val leftMotor: LinearFalconMotor
    val rightMotor: LinearFalconMotor

    val trajectoryTracker: TrajectoryTracker

    val robotPosition: Pose2d

    fun setOutput(output: TrajectoryTrackerOutput)

    @JvmDefault
    fun zeroOutputs() {
        leftMotor.setNeutral()
        rightMotor.setNeutral()
    }
}

data class TrajectoryTrackerOutput internal constructor(
    internal val _linearVelocity: Double,
    internal val _linearAcceleration: Double,
    internal val _angularVelocity: Double,
    internal val _angularAcceleration: Double
) {

    val linearVelocity get() = _linearVelocity.meter.velocity
    val linearAcceleration get() = _linearAcceleration.meter.acceleration
    val angularVelocity get() = _angularVelocity.radian.velocity
    val angularAcceleration get() = _angularAcceleration.radian.acceleration

    val differentialDriveVelocity
        get() = DifferentialDrive.ChassisState(
            _linearVelocity,
            _angularVelocity
        )

    val differentialDriveAcceleration
        get() = DifferentialDrive.ChassisState(
            _linearAcceleration,
            _angularAcceleration
        )

    constructor(
        linearVelocity: LinearVelocity,
        linearAcceleration: LinearAcceleration,
        angularVelocity: AngularVelocity,
        angularAcceleration: AngularAcceleration
    ) : this(
        _linearVelocity = linearVelocity.value,
        _linearAcceleration = linearAcceleration.value,
        _angularVelocity = angularVelocity.value,
        _angularAcceleration = angularAcceleration.value
    )
}