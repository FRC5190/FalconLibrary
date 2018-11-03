package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import kotlin.properties.Delegates.observable

abstract class InstantCommand : FalconCommand() {
    init {
        executeFrequency = 0
        finishCondition += { true }
    }
}

class InstantRunnableCommand(private val runnable: suspend () -> Unit) : InstantCommand() {
    override suspend fun initialize() = runnable()
}

class PeriodicRunnableCommand(
    private val runnable: suspend () -> Unit,
    exitCondition: BooleanSource,
    runnableFrequency: Int = -1
) : FalconCommand() {
    init {
        this.executeFrequency = runnableFrequency
        finishCondition += exitCondition
    }

    override suspend fun execute() = runnable()
}

class ConditionCommand(
    condition: BooleanSource
) : FalconCommand() {
    init {
        executeFrequency = 0
        finishCondition += condition
    }
}

class DelayCommand(private val delaySource: Source<Time>) : FalconCommand() {

    constructor(delay: Time) : this(Source(delay))

    init {
        executeFrequency = 0
    }

    override suspend fun initialize() {
        withTimeout(delaySource())
    }
}

class EmptyCommand(vararg requiredSubsystems: FalconSubsystem) : FalconCommand(*requiredSubsystems) {
    init {
        executeFrequency = 0
    }
}

class ConditionalCommand(
    val condition: BooleanSource,
    val onTrue: FalconCommand?,
    val onFalse: FalconCommand? = null
) : FalconCommand() {

    override val wrappedValue: Command = WpiConditionalCommand()

    private inner class WpiConditionalCommand : edu.wpi.first.wpilibj.command.ConditionalCommand(
        onTrue?.wrappedValue,
        onFalse?.wrappedValue
    ), IWpiCommand {
        override var timeout by observable(0.second) { _, _, newValue ->
            setTimeout(newValue.second)
        }

        override fun condition(): Boolean = condition.invoke()

        override fun isFinished() = super.isTimedOut() || super.isFinished() || finishCondition()
    }
}