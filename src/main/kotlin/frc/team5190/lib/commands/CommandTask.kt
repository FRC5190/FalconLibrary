package frc.team5190.lib.commands

import frc.team5190.lib.utils.launchFrequency
import frc.team5190.lib.utils.statefulvalue.invokeWhenTrue
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.newFixedThreadPoolContext

open class CommandTask(val command: Command, private val onFinish: (CommandTask, Long) -> Unit) {
    companion object {
        protected val commandContext = newFixedThreadPoolContext(2, "Command Context")
    }

    private val finishSync = Any()
    private var state = State.CREATED
    private var finished = false

    enum class State {
        CREATED,
        RUNNING,
        STOPPED
    }

    private var executor: Job? = null

    suspend fun start0(startTime: Long) {
        assert(state != State.RUNNING) { "You tried to start ${command::class.java.simpleName} task twice." }
        assert(state == State.CREATED) { "You cannot reuse command tasks." }
        state = State.RUNNING

        command.startTime = startTime
        command.initialize0()
        command.finishConditionValue.invokeWhenTrue { handleFinish() }

        val frequency = command.executeFrequency
        if (frequency != 0) executor = launchFrequency(frequency, commandContext) {
            command.execute0()
        }
    }

    private fun handleFinish() = synchronized(finishSync) {
        assert(!finished) { "Got finish event twice." }
        finished = true

        val commandTimeout = command.timeoutConditionValue
        val stopTime = commandTimeout?.let {
            Math.min(command.startTime + it.unit.toNanos(it.delay), System.nanoTime())
        } ?: System.nanoTime()

        executor?.cancel()
        onFinish(this, stopTime)
    }

    suspend fun stop0() = synchronized(finishSync) {
        assert(state == State.RUNNING) { "You tried to stop a command task that isn't running" }
        state = State.STOPPED

        val forcedStop = !finished

        executor?.run {
            if (forcedStop) cancel()
            join()
        }
        executor = null
        stop()
        command.dispose0()
    }

    protected open fun stop() {}
}