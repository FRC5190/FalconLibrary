package frc.team5190.lib.commands

import frc.team5190.lib.utils.launchFrequency
import frc.team5190.lib.utils.statefulvalue.invokeOnceWhenTrue
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

open class CommandTask(val command: Command, private val onFinish: (CommandTask, Long) -> Unit) {
    companion object {
        protected val commandContext = newFixedThreadPoolContext(2, "Command Context")
    }

    // TODO figure out a way to not use coroutines that doesn't bug
    private val finishSync = Mutex()
    private var state = State.CREATED
    private var finished = false

    enum class State {
        CREATED,
        RUNNING,
        STOPPED
    }

    private var executor: Job? = null

    suspend fun start0(startTime: Long) {
        assert(state != State.RUNNING) { "You tried to startTask ${command::class.java.simpleName} task twice." }
        assert(state == State.CREATED) { "You cannot reuse command tasks." }
        state = State.RUNNING

        command.startTime = startTime
        command.initialize0()
        command.finishConditionValue.invokeOnceWhenTrue {
            runBlocking(commandContext) {
                handleFinish()
            }
        }

        val frequency = command.executeFrequency
        if (frequency != 0) executor = launchFrequency(frequency, commandContext) {
            command.execute0()
        }
    }

    private suspend fun handleFinish() = finishSync.withLock {
        assert(!finished) { "Got finish event twice." }
        finished = true

        val commandTimeout = command.timeoutConditionValue
        val stopTime = commandTimeout?.let {
            Math.min(command.startTime + it.unit.toNanos(it.delay), System.nanoTime())
        } ?: System.nanoTime()

        executor?.cancel()
        onFinish(this, stopTime)
    }

    suspend fun stop0(stopTime: Long) = finishSync.withLock {
        assert(state == State.RUNNING) { "You tried to stop a command task that isn't running" }
        state = State.STOPPED
        try {
            executor?.cancelAndJoin()
        } catch (e: CancellationException) {
            e.printStackTrace()
        }
        executor = null
        command.dispose0()
        stop(stopTime)
    }

    protected open fun stop(stopTime: Long) {}
}