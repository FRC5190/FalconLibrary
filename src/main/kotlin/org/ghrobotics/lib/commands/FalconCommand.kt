package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import kotlinx.coroutines.experimental.*
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.loopFrequency

abstract class FalconCommand(val requiredSubsystems: List<FalconSubsystem>) : AbstractFalconCommand() {
    companion object {
        const val DEFAULT_FREQUENCY = 50

        protected val commandScope = CoroutineScope(newFixedThreadPoolContext(2, "Command"))
    }

    constructor(vararg requiredSubsystems: FalconSubsystem) : this(requiredSubsystems.toList())

    private val _wpiCommand = FalconWpiCommand()
    override val wpiCommand: Command = _wpiCommand

    private inner class FalconWpiCommand : Command() {

        init {
            requiredSubsystems.forEach {
                requires(it.wpiSubsystem)
            }
        }

        var timeout = 0.second
            set(value) {
                setTimeout(value.second.asDouble)
                field = value
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

        override fun isFinished() = _finishCondition.value

    }

    protected var executeFrequency = DEFAULT_FREQUENCY

    override fun withTimeout(delay: Time) = apply { _wpiCommand.timeout = delay }

}

object EmptyFalconCommand : FalconCommand() {
    init {
        executeFrequency = 0
    }
}