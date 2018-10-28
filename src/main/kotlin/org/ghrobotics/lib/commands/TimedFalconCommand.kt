package org.ghrobotics.lib.commands

import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second

abstract class TimedFalconCommand(
        vararg requiredSubsystems: FalconSubsystem
) : FalconCommand(*requiredSubsystems) {

    private var startTime: Long = 0L
    private var lastExecute: Long = 0L
    private val executeCommandScope = ExecuteCommandScopeImpl()

    override suspend fun initialize0() {
        super.initialize0()
        startTime = System.nanoTime()
        lastExecute = startTime
        executeCommandScope.startTime = startTime.nanosecond
        executeCommandScope.deltaTime = 0.second
    }

    final override suspend fun execute() {
        val newTime = System.nanoTime()
        executeCommandScope.deltaTime = (newTime - lastExecute).nanosecond
        executeCommandScope.timeElapsed = (newTime - startTime).nanosecond
        executeCommandScope.run { timedExecute() }
        lastExecute = newTime
    }

    protected open suspend fun ExecuteCommandScope.timedExecute() {}

    private inner class ExecuteCommandScopeImpl : ExecuteCommandScope() {
        override lateinit var startTime: Time
        override lateinit var timeElapsed: Time
        override lateinit var deltaTime: Time
    }

    protected abstract inner class ExecuteCommandScope {
        abstract val startTime: Time
        abstract val timeElapsed: Time
        abstract val deltaTime: Time
    }
}