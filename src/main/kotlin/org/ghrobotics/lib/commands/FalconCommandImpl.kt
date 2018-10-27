package org.ghrobotics.lib.commands

import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableValue

abstract class InstantCommand : FalconCommand() {
    override fun CreateCommandScope.create() {
        executeFrequency = 0
        finishCondition += ObservableValue(true)
    }
}

class InstantRunnableCommand(private val runnable: suspend () -> Unit) : InstantCommand() {
    override suspend fun InitCommandScope.initialize() = runnable()
}

class PeriodicRunnableCommand(
    private val runnable: suspend () -> Unit,
    private val exitCondition: ObservableValue<Boolean>,
    private val executeFrequency: Int = DEFAULT_FREQUENCY
) : FalconCommand() {
    override fun CreateCommandScope.create() {
        this.executeFrequency = this@PeriodicRunnableCommand.executeFrequency
        finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}

class ConditionCommand(
    private val condition: ObservableValue<Boolean>
) : FalconCommand() {
    override fun CreateCommandScope.create() {
        finishCondition += condition
    }
}

class DelayCommand(private val delaySource: Source<Time>) : FalconCommand() {
    override suspend fun InitCommandScope.initialize() {
        executeFrequency = 0
        withTimeout(delaySource.value)
    }
}

class EmptyCommand(vararg requiredSubsystems: FalconSubsystem) : FalconCommand(*requiredSubsystems) {
    override suspend fun InitCommandScope.initialize() {
        executeFrequency = 0
    }
}