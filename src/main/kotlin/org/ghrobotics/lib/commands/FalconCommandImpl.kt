package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import kotlin.properties.Delegates.observable

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
    private val runnableFrequency: Int = DEFAULT_FREQUENCY
) : FalconCommand() {
    override fun CreateCommandScope.create() {
        this.executeFrequency = runnableFrequency
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

    constructor(delay: Time) : this(Source(delay))

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
            setTimeout(newValue.second.asDouble)
        }

        override fun condition(): Boolean = condition.value

        override fun isFinished() = super.isFinished() || finishCondition.value
    }

}