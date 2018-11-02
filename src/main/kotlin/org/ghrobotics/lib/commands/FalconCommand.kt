package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import kotlinx.coroutines.*
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.BooleanSource
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.loopFrequency
import org.ghrobotics.lib.utils.or
import org.ghrobotics.lib.wrappers.FalconRobotBase
import org.ghrobotics.lib.wrappers.Wrapper
import kotlin.properties.Delegates.observable

abstract class FalconCommand(
        vararg requiredSubsystems: FalconSubsystem
) : Wrapper<Command> {

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized)
            println("[FalconCommand} [WARNING] It is not recommended to create commands after the robot has initialized!")
    }

    override val wrappedValue: Command = WpiCommand(requiredSubsystems)

    protected val finishCondition = FinishCondition(Source(false))
    protected var executeFrequency = -1

    internal open suspend fun initialize0() = initialize()
    internal open suspend fun execute0() = execute()
    internal open suspend fun dispose0() = dispose()

    protected open suspend fun initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start() = wrappedValue.start()
    fun stop() = wrappedValue.cancel()

    protected class FinishCondition(
            private var condition: BooleanSource
    ) : BooleanSource {
        override fun invoke() = condition()

        operator fun plusAssign(other: BooleanSource) {
            condition = condition or other
        }

        fun set(other: BooleanSource) {
            condition = other
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

        private var job: Job? = null
        private var useCommandLoop = false

        override fun initialize() {
            val frequency = executeFrequency
            if (frequency > 0) {
                useCommandLoop = false
                job = commandScope.launch {
                    initialize0()
                    loopFrequency(frequency) {
                        execute0()
                    }
                }
            } else {
                runBlocking { initialize0() }
                useCommandLoop = frequency < 0
            }
        }

        override fun execute() {
            if (useCommandLoop) runBlocking { execute0() }
        }

        override fun end() = runBlocking {
            job?.cancelAndJoin()
            dispose0()
        }

        override fun isFinished() = finishCondition()
    }

    fun withExit(condition: BooleanSource) = also { finishCondition += condition }
    fun overrideExit(condition: BooleanSource) = also { finishCondition.set(condition) }
    fun withTimeout(delay: Time) = also { (wrappedValue as IWpiCommand).timeout = delay }

    companion object {
        protected val commandScope = CoroutineScope(newFixedThreadPoolContext(2, "Command"))
    }
}



