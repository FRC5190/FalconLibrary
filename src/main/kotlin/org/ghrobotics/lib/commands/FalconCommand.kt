package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import kotlinx.coroutines.*
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.loopFrequency
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableValueReference
import org.ghrobotics.lib.utils.observabletype.or
import org.ghrobotics.lib.utils.observabletype.updatableValue
import org.ghrobotics.lib.wrappers.FalconRobotBase
import org.ghrobotics.lib.wrappers.Wrapper
import kotlin.properties.Delegates.observable

abstract class FalconCommand(
        vararg requiredSubsystems: FalconSubsystem
) : Wrapper<Command> {

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[FalconCommand} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
        create0()
    }

    override val wrappedValue: Command = WpiCommand(requiredSubsystems)

    val commandState by lazy {
        GlobalScope.updatableValue {
            when {
                wrappedValue.isRunning -> CommandState.BAKING
                wrappedValue.isCompleted -> CommandState.BAKED
                else -> CommandState.PREPARED
            }
        }
    }

    private val _finishCondition = FinishCondition()
    val finishCondition: ObservableValue<Boolean> = _finishCondition
    var executeFrequency = DEFAULT_FREQUENCY
        private set

    private fun create0() = CreateCommandScope().run { create() }
    protected open suspend fun initialize0() = InitCommandScope().run { initialize() }

    private suspend fun execute0() = execute()
    private suspend fun dispose0() = dispose()

    protected open fun CreateCommandScope.create() {}
    protected open suspend fun InitCommandScope.initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start() = wrappedValue.start()
    fun stop() = wrappedValue.cancel()

    protected class FinishCondition private constructor(
            private val varReference: ObservableValueReference<Boolean>
    ) : ObservableValue<Boolean> by varReference {
        constructor() : this(ObservableValueReference(ObservableValue(false)))

        operator fun plusAssign(other: ObservableValue<Boolean>) {
            varReference.reference = varReference.reference or other
        }

        operator fun plusAssign(other: BooleanSource) = plusAssign(GlobalScope.updatableValue(block = other))

        fun set(other: ObservableValue<Boolean>) {
            varReference.reference = other
        }

        override fun toString() = "FINISH($varReference)[$value]"
    }

    // Different scopes for allowing different amount of controls depending on which method you are using

    protected inner class CreateCommandScope {
        val finishCondition = this@FalconCommand._finishCondition
        var executeFrequency by observable(this@FalconCommand.executeFrequency) { _, _, newValue ->
            this@FalconCommand.executeFrequency = newValue
        }
    }

    protected inner class InitCommandScope {
        var executeFrequency by observable(this@FalconCommand.executeFrequency) { _, _, newValue ->
            this@FalconCommand.executeFrequency = newValue
        }
    }

    // Wrapped Command

    protected interface IWpiCommand {
        var timeout: Time
    }

    protected inner class WpiCommand(
            requiredSubsystems: Array<out FalconSubsystem>
    ) : Command(), IWpiCommand {
        init {
            requiredSubsystems.forEach { requires(it.wpiSubsystem) }
        }

        override var timeout by observable(0.second) { _, _, newValue ->
            setTimeout(newValue.second)
        }

        private lateinit var job: Job

        override fun initialize() {
            job = commandScope.launch {
                initialize0()
                val frequency = executeFrequency
                if (frequency != 0) loopFrequency(frequency) {
                    execute0()
                }
            }
        }

        override fun end() = runBlocking {
            job.cancelAndJoin()
            dispose0()
        }

        override fun isFinished() = finishCondition.value
    }

    fun withExit(condition: ObservableValue<Boolean>) = also { _finishCondition += condition }
    fun withExit(condition: BooleanSource) = withExit(GlobalScope.updatableValue(block = condition))
    fun overrideExit(condition: ObservableValue<Boolean>) = also { _finishCondition.set(condition) }
    fun withTimeout(delay: Time) = also { (wrappedValue as IWpiCommand).timeout = delay }

    companion object {
        const val DEFAULT_FREQUENCY = 50

        protected val commandScope = CoroutineScope(newFixedThreadPoolContext(2, "Command"))
    }
}



