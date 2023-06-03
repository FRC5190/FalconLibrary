/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.subsystems.drive.swerve

import com.pathplanner.lib.PathPlannerTrajectory
import edu.wpi.first.math.geometry.Pose2d
import edu.wpi.first.math.geometry.Rotation2d
import edu.wpi.first.math.kinematics.SwerveDriveKinematics
import edu.wpi.first.math.kinematics.SwerveModulePosition
import edu.wpi.first.math.kinematics.SwerveModuleState
import edu.wpi.first.math.trajectory.Trajectory
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.Field2d
import edu.wpi.first.wpilibj2.command.Command
import org.ghrobotics.lib.mathematics.twodim.trajectory.mirror
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.Meter
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.LinearVelocity
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.subsystems.SensorlessCompatibleSubsystem
import org.ghrobotics.lib.subsystems.drive.FalconDriveHelper
import org.ghrobotics.lib.utils.Source

abstract class FalconSwerveDrivetrain :
    TrajectoryTrackerSwerveDriveBase(),
    SensorlessCompatibleSubsystem {
    /**
     * The current inputs and outputs
     */
    abstract val swerveDriveIO: SwerveDriveIO
    abstract val abstractSwerveDriveInputs: AbstractSwerveDriveInputs

    /**
     * Helper for different drive styles.
     */
    protected val driveHelper = FalconDriveHelper()

    abstract val wheelbase: Double

    abstract val trackWidth: Double

    abstract val maxSpeed: SIUnit<Velocity<Meter>>

    abstract val motorOutputLimiter: Source<Double>

    /**
     * Get the robot's position on the field. Up to the user to update the position.
     */
    override var robotPosition: Pose2d = Pose2d()

    val field = Field2d()
    private val fieldTab = Shuffleboard.getTab("Field")

    abstract fun resetPosition(pose: Pose2d, positions: Array<SwerveModulePosition>)

    fun resetPosition(newPose: Pose2d) {
        resetPosition(newPose, swerveDriveIO.positions)
    }

    fun setTrajectory(traj: Trajectory) {
        field.getObject("traj").setTrajectory(traj)
    }

    /**
     * Represents periodic data
     */
    override fun lateInit() {
        resetPosition(Pose2d(), swerveDriveIO.positions)
        fieldTab.add("Field", field).withSize(8, 4)
    }

    override fun setNeutral() {
        swerveDriveIO.setNeutral()
    }

    override fun setOutputSI(
        states: Array<SwerveModuleState>,
    ) {
        swerveDriveIO.setModuleStates(states)
    }

    fun swerveDrive(forwardInput: Double, strafeInput: Double, rotationInput: Double, fieldRelative: Boolean = true) {
        val outputLimiter = motorOutputLimiter()
        val speeds = driveHelper.swerveDrive(
            this,
            forwardInput * outputLimiter,
            strafeInput * outputLimiter,
            rotationInput * outputLimiter * .75,
            fieldRelative,
        )
        val states = kinematics.toSwerveModuleStates(speeds)
        SwerveDriveKinematics.desaturateWheelSpeeds(states, maxSpeed.value)
        swerveDriveIO.setModuleStates(states)
    }

    fun followTrajectory(trajectory: PathPlannerTrajectory, mirrored: Boolean = false) = SwerveTrajectoryTrackerCommand(
        this,
        Source((if (mirrored) trajectory.mirror() else trajectory) as PathPlannerTrajectory),
    )

    fun followTrajectory(trajectory: Source<PathPlannerTrajectory>) = SwerveTrajectoryTrackerCommand(this, trajectory)

    fun followTrajectoryWithCommands(trajectory: PathPlannerTrajectory, eventMap: HashMap<String, Command>) =
        SwerveTrajectoryTrackerWithMarkersCommand(this, trajectory, eventMap)

    fun followTrajectoryGroupWithCommands(
        trajectories: List<PathPlannerTrajectory>,
        eventMap: HashMap<String, Command>,
    ) = SwerveTrajectoryGroupTrackerCommand(this, trajectories, eventMap)

    val List<AbstractFalconSwerveModule<*, *>>.positions: List<SwerveModulePosition>
        get() = List(4) {
            SwerveModulePosition(
                this[it].drivePosition.value,
                Rotation2d(this[it].encoder.absolutePosition.value),
            )
        }
}

interface SwerveDriveIO {

    fun <T : AbstractSwerveDriveInputs> updateInputs(inputs: T)
    fun setModuleStates(states: Array<SwerveModuleState>)
    fun setNeutral()

    val positions: Array<SwerveModulePosition>

    val states: Array<SwerveModuleState>

    val gyro: Source<Rotation2d>
}

interface AbstractSwerveDriveInputs {
    var leftFrontVoltage: SIUnit<Volt>
    var rightFrontVoltage: SIUnit<Volt>
    var rightBackVoltage: SIUnit<Volt>
    var leftBackVoltage: SIUnit<Volt>

    var leftFrontCurrent: SIUnit<Ampere>
    var rightFrontCurrent: SIUnit<Ampere>
    var rightBackCurrent: SIUnit<Ampere>
    var leftBackCurrent: SIUnit<Ampere>

    var leftFrontPosition: SIUnit<Meter>
    var rightFrontPosition: SIUnit<Meter>
    var rightBackPosition: SIUnit<Meter>
    var leftBackPosition: SIUnit<Meter>

    var leftFrontRotation: SIUnit<Radian>
    var rightFrontRotation: SIUnit<Radian>
    var rightBackRotation: SIUnit<Radian>
    var leftBackRotation: SIUnit<Radian>

    var leftFrontVelocity: SIUnit<LinearVelocity>
    var rightFrontVelocity: SIUnit<LinearVelocity>
    var rightBackVelocity: SIUnit<LinearVelocity>
    var leftBackVelocity: SIUnit<LinearVelocity>

    var leftFrontFeedforward: SIUnit<Volt>
    var rightFrontFeedforward: SIUnit<Volt>
    var rightBackFeedforward: SIUnit<Volt>
    var leftBackFeedforward: SIUnit<Volt>

    var gyroRaw: SIUnit<Radian>
}
