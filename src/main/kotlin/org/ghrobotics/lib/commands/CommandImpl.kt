package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableValue

abstract class InstantCommand : FalconCommand() {
    init {
        executeFrequency = 0
        _finishCondition += ObservableValue(true)
    }
}

class InstantRunnableCommand(private val runnable: suspend () -> Unit) : InstantCommand() {
    override suspend fun initialize() = runnable()
}

class PeriodicRunnableCommand(
        private val runnable: suspend () -> Unit,
        exitCondition: ObservableValue<Boolean>,
        executeFrequency: Int = FalconCommand.DEFAULT_FREQUENCY
) : FalconCommand() {
    init {
        this.executeFrequency = executeFrequency
        _finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}