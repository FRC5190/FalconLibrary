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
import kotlin.properties.Delegates.observable

/**
 *  Kotlin Wrapper for [WPI's Command][Command]
 *  @param requiredSubsystems subsystems this command requires in order to run
 */
abstract class FalconCommand(
    requiredSubsystems: Iterable<FalconSubsystem>
) {

    constructor(vararg requiredSubsystems: FalconSubsystem) : this(requiredSubsystems.asIterable())

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[FalconCommand} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
    }

    /**
     *  Wrapped WPI command
     */
    open val wrappedValue: Command = WpiCommand(requiredSubsystems)

    /**
     *  When this is true the command will end
     */
    protected val finishCondition = FinishCondition(Source(false))
    /**
     * The frequency the command will run
     * -1 -> will use WPI's internal command loop
     * 0 -> will not call the execute block
     */
    protected var executeFrequency = -1

    internal open suspend fun initialize0() = initialize()
    internal open suspend fun execute0() = execute()
    internal open suspend fun dispose0() = dispose()

    /**
     * Called when the command starts
     */
    protected open suspend fun initialize() {}

    /**
     * Called every command loop while the command is running
     */
    protected open suspend fun execute() {}

    /**
     * Called when the command stops
     */
    protected open suspend fun dispose() {}

    /**
     * Starts the command
     */
    fun start() = wrappedValue.start()

    /**
     * Stops the command, if running
     */
    fun stop() = wrappedValue.cancel()

    protected class FinishCondition(
        private var condition: BooleanSource
    ) : BooleanSource {
        override fun invoke() = condition()

        /**
         *  Adds another possible Finish Condition
         *  @param other possible finish condition
         */
        operator fun plusAssign(other: BooleanSource) {
            condition = condition or other
        }

        /**
         * Overrides the finish condition
         * @param other the new finish condition
         */
        fun set(other: BooleanSource) {
            condition = other
        }
    }

    // Wrapped Command

    protected interface IWpiCommand {
        var timeout: Time
    }

    protected inner class WpiCommand(
        requiredSubsystems: Iterable<FalconSubsystem>
    ) : Command(), IWpiCommand {
        init {
            requiredSubsystems.forEach { requires(it.wpiSubsystem) }
        }

        override var timeout by observable(0.second) { _, _, newValue ->
            setTimeout(newValue.second)
        }

        private var job: Job? = null
        private var useCommandLoop = false

        @ObsoleteCoroutinesApi
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

        override fun isFinished() = super.isTimedOut() || finishCondition()
    }

    /**
     *  Adds another possible finish condition to the command
     *  @param condition possible finish condition
     */
    fun withExit(condition: BooleanSource) = also { finishCondition += condition }

    /**
     * Overrides the finish condition
     * @param condition the new finish condition
     */
    fun overrideExit(condition: BooleanSource) = also { finishCondition.set(condition) }

    /**
     * Sets the timeout for the command
     * @param timeout the timeout for the command, the command will never run longer then this timeout
     */
    fun withTimeout(timeout: Time) = also { (wrappedValue as? IWpiCommand)?.timeout = timeout }

    companion object {
        @ObsoleteCoroutinesApi
        protected val commandScope = CoroutineScope(newFixedThreadPoolContext(2, "Command"))
    }
}