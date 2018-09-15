package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableHandle
import org.ghrobotics.lib.utils.observabletype.invokeOnceWhenTrue
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

open class CommandTask(val command: Command, private val onFinish: (CommandTask, Long) -> Unit) {
    // TODO figure out a way to not use coroutines that doesn't bug
    private val finishSync = Mutex()
    private var state = State.CREATED
    private var finished = false
    private lateinit var finishHandle: ObservableHandle

    enum class State {
        CREATED,
        RUNNING,
        STOPPED
    }

    suspend fun start0(startTime: Long) {
        assert(state != State.RUNNING) { "You tried to startTask ${command::class.java.simpleName} task twice." }
        assert(state == State.CREATED) { "You cannot reuse command tasks." }
        state = State.RUNNING

        command.startTime = startTime
        command.initialize0()
        finishHandle = command.finishCondition.invokeOnceWhenTrue {
            runBlocking {
                finishSync.withLock {
                    assert(!finished) { "Got finish event twice." }
                    finished = true

                    val commandTimeout = command.timeoutCondition
                    val stopTime = commandTimeout?.let {
                        Math.min(command.startTime + it.unit.toNanos(it.delay), System.nanoTime())
                    } ?: System.nanoTime()

                    onFinish(this@CommandTask, stopTime)
                }
            }
        }
    }

    suspend fun stop0(stopTime: Long) {
        finishHandle.dispose()
        finishSync.withLock {
            assert(state == State.RUNNING) { "You tried to stop a command task that isn't running" }
            state = State.STOPPED
            command.dispose0()
            stop(stopTime)
        }
    }

    protected open fun stop(stopTime: Long) {}
}