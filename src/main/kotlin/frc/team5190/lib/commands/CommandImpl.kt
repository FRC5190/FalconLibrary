package frc.team5190.lib.commands

import frc.team5190.lib.utils.constState

abstract class InstantCommand : Command() {
    init {
        updateFrequency = 0
        finishCondition += constState(true)
    }
}

class InstantRunnableCommand(private val runnable: suspend () -> Unit) : InstantCommand() {
    override suspend fun initialize() = runnable()
}

class PeriodicRunnableCommand(
        private val runnable: suspend () -> Unit,
        exitCondition: Condition,
        updateFrequency: Int = Command.DEFAULT_FREQUENCY
) : Command(updateFrequency) {
    init {
        finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}