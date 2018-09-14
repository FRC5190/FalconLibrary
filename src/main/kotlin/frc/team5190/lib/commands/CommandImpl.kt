package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.StatefulBoolean
import frc.team5190.lib.utils.statefulvalue.StatefulValue

abstract class InstantCommand : Command() {
    init {
        executeFrequency = 0
        finishCondition += StatefulValue(true)
    }
}

class InstantRunnableCommand(private val runnable: suspend () -> Unit) : InstantCommand() {
    override suspend fun initialize() = runnable()
}

class PeriodicRunnableCommand(
        private val runnable: suspend () -> Unit,
        exitCondition: StatefulBoolean,
        executeFrequency: Int = Command.DEFAULT_FREQUENCY
) : Command() {
    init {
        this.executeFrequency = executeFrequency
        finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}