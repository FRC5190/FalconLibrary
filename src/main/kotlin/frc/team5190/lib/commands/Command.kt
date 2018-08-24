package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.*
import frc.team5190.lib.wrappers.FalconRobotBase
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

abstract class Command(val requiredSubsystems: List<Subsystem>) {
    companion object {
        const val DEFAULT_FREQUENCY = 50
    }

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[Command} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
    }

    constructor(vararg requiredSubsystems: Subsystem) : this(requiredSubsystems.toList())

    var executeFrequency = DEFAULT_FREQUENCY
        protected set

    @Deprecated("Execute frequency makes more sense")
    var updateFrequency
        get() = executeFrequency
        set(value) {
            executeFrequency = value
        }

    protected val finishCondition = StatefulFinishCondition(StatefulValue(false))
    internal val finishConditionValue: StatefulBoolean = finishCondition

    private var timeoutCondition: StatefulDelayImpl? = null
    internal val timeoutConditionValue: StatefulDelay?
        get() = timeoutCondition

    private val commandState = StatefulVariable(CommandState.PREPARED)
    val commandStateValue: StatefulValue<CommandState> = commandState

    var startTime = 0L
        internal set

    /**
     * Is true when all the finish conditions are met
     */
    fun isFinished() = finishCondition.value

    open suspend fun initialize0() {
        commandState.value = CommandState.BAKING
        initialize()
        timeoutCondition?.start(startTime)
    }

    open suspend fun execute0() = execute()
    open suspend fun dispose0() {
        timeoutCondition?.stop()
        dispose()
        commandState.value = CommandState.BAKED
    }

    protected open suspend fun initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start(): Deferred<Unit> {
        if (commandState.value == CommandState.BAKING) {
            println("[Command] ${this::class.java.simpleName} is already running, discarding start.")
            return CompletableDeferred(Unit)
        }
        if (commandState.value == CommandState.QUEUED) {
            println("[Command] ${this::class.java.simpleName} is already queued, discarding start.")
            return CompletableDeferred(Unit)
        }
        commandState.value = CommandState.QUEUED
        return CommandHandler.start(this, System.nanoTime())
    }

    fun stop() = CommandHandler.stop(this, System.nanoTime())

    fun withExit(condition: StatefulBoolean) = also { finishCondition += condition }
    fun withTimeout(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) = also {
        if (timeoutCondition == null) {
            timeoutCondition = StatefulDelayImpl(delay, unit)
            finishCondition += timeoutCondition!!
        } else {
            timeoutCondition!!.delay = delay
            timeoutCondition!!.unit = unit
        }
    }

    suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        cont.disposeOnCancellation(commandState.invokeOnceWhenFinished {
            cont.resume(Unit)
        })
    }

    // Little cheat so you don't have to reassign finishCondition every time you modify it
    protected class StatefulFinishCondition(private var currentCondition: StatefulBoolean) : StatefulValue<Boolean> {
        private val usedSync = Any()
        private var used = false

        override val value: Boolean
            get() = currentCondition.value

        override fun openSubscription(context: CoroutineContext): ReceiveChannel<Boolean> = synchronized(usedSync) {
            used = true
            currentCondition.openSubscription(context)
        }

        override fun invokeOnChange(context: CoroutineContext, invokeFirst: Boolean, listener: StatefulListener<Boolean>): DisposableHandle = synchronized(usedSync) {
            used = true
            // Fix for non-subscribable stateful constants (they don't change)
            return if (currentCondition is StatefulConstant) currentCondition.invokeOnChange(context, invokeFirst, listener)
            else super.invokeOnChange(context, invokeFirst, listener)
        }

        /**
         * Shortcut operator for the or function
         */
        operator fun plusAssign(condition: StatefulBoolean): Unit = synchronized(usedSync) {
            if (used) throw IllegalStateException("Cannot add condition once a listener has been added")
            currentCondition = currentCondition or condition
        }
    }

}
