package org.ghrobotics.lib.components

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.motors.FalconMotor
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


    protected var arbitraryFeedForward: Double = 0.0

    open fun customizeWantedState(wantedState: State): State = wantedState

    override fun updateState() {
        position = motor.encoder.position
        velocity = motor.encoder.velocity

        super.updateState()
    }

    override fun useState() {

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

        super.useState()
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