package frc.team5190.lib.utils

import kotlinx.coroutines.experimental.*

class StateMachine<T>(val state: State<T>) {

    companion object {
        private val stateMachineContext = newFixedThreadPoolContext(2, "State Machine")
    }

    fun onEnter(enterState: List<T>, listener: SMEnterListener<T>) = state.invokeWhen(enterState) {
        runBlocking(stateMachineContext) { listener(it) }
    }

    fun onLeave(leaveState: List<T>, listener: SMLeaveListener<T>) = state.invokeOnChange {
        val from = state.value
        if (leaveState.contains(from)) runBlocking(stateMachineContext) { listener(from) }
    }

    fun onTransition(fromState: List<T>, toState: List<T>, listener: SMTransitionListener<T>) = state.invokeOnChange {
        val from = state.value
        if (fromState.contains(from) && toState.contains(it)) {
            runBlocking(stateMachineContext) { listener(from, it) }
        }
    }

    fun onWhile(whileState: List<T>, frequency: Int = 50, listener: SMWhileListener<T>): DisposableHandle {
        var job: Job? = null
        var currentValue: T
        val handle1 = state.invokeWhen(whileState) {
            currentValue = it
            if (job != null) return@invokeWhen
            job = launchFrequency(frequency, stateMachineContext) {
                listener(currentValue)
            }
        }
        val handle2 = state.invokeOnChange {
            if (!whileState.contains(it)) runBlocking(stateMachineContext) {
                job?.cancelAndJoin()
                job = null
            }
        }
        return object : DisposableHandle {
            override fun dispose() {
                handle1.dispose()
                handle2.dispose()
                job?.cancel()
                job = null
            }
        }
    }

}

typealias SMEnterListener<T> = suspend (to: T) -> Unit
typealias SMLeaveListener<T> = suspend (from: T) -> Unit
typealias SMTransitionListener<T> = suspend (from: T, to: T) -> Unit
typealias SMWhileListener<T> = suspend (state: T) -> Unit