package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableValue

abstract class InstantCommand : Command() {
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
        executeFrequency: Int = Command.DEFAULT_FREQUENCY
) : Command() {
    init {
        this.executeFrequency = executeFrequency
        _finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}