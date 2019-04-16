package org.ghrobotics.lib.components

import edu.wpi.first.wpilibj.Timer
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.motors.FalconMotor
import org.ghrobotics.lib.utils.DeltaTime
import org.ghrobotics.lib.utils.Source

abstract class MotorComponent<T : SIUnit<T>> : RobotComponent() {

    abstract val motor: FalconMotor<T>

    var wantedState: State = State.Nothing
    var currentState: State = State.Nothing
        private set

    var position: Double = 0.0
        private set
    var velocity: Double = 0.0
        private set
    var acceleration: Double = 0.0
        private set

    private val loopDeltaTime = DeltaTime()

    protected var arbitraryFeedForward: Double = 0.0

    open fun customizeWantedState(wantedState: State): State = wantedState

    override fun update() {
        super.update()

        val dt = loopDeltaTime.updateTime(Timer.getFPGATimestamp())

        position = motor.encoder.position
        val previousVelocity = velocity
        velocity = motor.encoder.velocity
        acceleration = if (dt <= 0.0) {
            (velocity - previousVelocity) / dt
        } else {
            0.0
        }

        val wantedState = customizeWantedState(wantedState)
        currentState = wantedState
        when (wantedState) {
            is State.Nothing -> motor.setNeutral()
            is State.Position -> motor.setPosition(wantedState.position, arbitraryFeedForward)
            is State.PercentOutput -> motor.setDutyCycle(
                wantedState.output(),
                if (wantedState.useFeedForward) arbitraryFeedForward else 0.0
            )
            is State.CustomState -> wantedState.update(this, arbitraryFeedForward)
        }
    }

    sealed class State {
        object Nothing : State()
        class Position(val position: Double) : State()
        class PercentOutput(val output: Source<Double>, val useFeedForward: Boolean) : State() {
            constructor(output: Double, useFeedForward: Boolean) : this(Source(output), useFeedForward)
        }

        abstract class CustomState : State() {
            abstract fun update(motorComponent: MotorComponent<*>, arbitraryFeedForward: Double)
        }
    }

}