package org.ghrobotics.lib.components

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.threedim.geometry.Pose3d
import org.ghrobotics.lib.mathematics.threedim.geometry.Quaternion
import org.ghrobotics.lib.mathematics.threedim.geometry.Translation3d
import org.ghrobotics.lib.mathematics.units.Length
import org.ghrobotics.lib.subsystems.drive.DifferentialTrackerDriveBase
import org.ghrobotics.lib.utils.DeltaTime
import org.ghrobotics.lib.utils.Source

abstract class DriveComponent(
    private val drivetrainHeightFromGround: Double
) : RobotComponent(), DifferentialTrackerDriveBase {

    constructor(drivetrainHeightFromGround: Length) : this(drivetrainHeightFromGround.value)

    var wantedState: State =
        State.Nothing
    var currentState: State =
        State.Nothing
        private set

    open fun customizeWantedState(wantedState: State): State = wantedState

    private val loopDeltaTime = DeltaTime()

    override fun updateState() {

        val dt = loopDeltaTime.updateTime(Timer.getFPGATimestamp())

        val robotPose = robotPosition

        val lastLocalTransform = localTransform

        localTransform = Pose3d(
            Translation3d(robotPose.translation.x, robotPose.translation.y, drivetrainHeightFromGround),
            Quaternion.fromEulerAngles(robotPose.rotation.radian, 0.0, 0.0)
        )

        localVelocityTransform = (lastLocalTransform - localTransform) / dt

        super.updateState()
    }

    override fun useState() {

        val wantedState = customizeWantedState(wantedState)
        currentState = wantedState
        when (wantedState) {
            is State.Nothing -> {
                leftMotor.setNeutral()
                rightMotor.setNeutral()
            }
            is State.Velocity -> {
                leftMotor.setVelocity(wantedState.leftVelocity())
                rightMotor.setVelocity(wantedState.rightVelocity())
            }
            is State.PercentOutput -> {
                leftMotor.setDutyCycle(wantedState.leftOutput())
                rightMotor.setDutyCycle(wantedState.rightOutput())
            }
            is State.CustomState -> wantedState.update()
        }

        super.useState()
    }

    sealed class State {
        object Nothing : State()
        class Velocity(val leftVelocity: Source<Double>, val rightVelocity: Source<Double>) : State() {
            constructor(leftVelocity: Double, rightVelocity: Double) : this(Source(leftVelocity), Source(rightVelocity))
        }

        class PercentOutput(val leftOutput: Source<Double>, val rightOutput: Source<Double>) : State() {
            constructor(leftOutput: Double, rightOutput: Double) : this(Source(leftOutput), Source(rightOutput))
        }

        abstract class CustomState : State() {
            abstract fun update()
        }
    }

}