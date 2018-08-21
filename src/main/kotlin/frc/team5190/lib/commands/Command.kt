package frc.team5190.lib.commands

import frc.team5190.lib.utils.State
import frc.team5190.lib.utils.StateImpl
import frc.team5190.lib.utils.StateListener
import frc.team5190.lib.utils.constState
import frc.team5190.lib.wrappers.FalconRobotBase
import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit

abstract class Command(updateFrequency: Int = DEFAULT_FREQUENCY) {
    companion object {
        const val DEFAULT_FREQUENCY = 50
    }

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[Command} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
    }

    var updateFrequency = updateFrequency
        protected set

    internal open val requiredSubsystems: List<Subsystem> = mutableListOf()

    protected val finishCondition = CommandCondition(constState(false))
    internal val exposedCondition: Condition
        get() = finishCondition

    private var timeoutCondition: DelayCondition? = null
    var timeout = 0L to TimeUnit.SECONDS
        private set

    val commandState: State<CommandState> = CommandStateImpl()

    var startTime = 0L
        internal set

    /**
     * Is true when all the finish conditions are met
     */
    fun isFinished() = finishCondition.value

    // Little cheat so you don't have to reassign finishCondition every time you modify it
    protected class CommandCondition(private var currentCondition: Condition) : State<Boolean> {
        private var used = false

        override val value: Boolean
            get() = currentCondition.value

        override fun invokeOnChange(listener: StateListener<Boolean>): DisposableHandle = synchronized(this) {
            used = true
            return currentCondition.invokeOnChange(listener)
        }

        override fun invokeWhen(state: List<Boolean>, ignoreCurrent: Boolean, listener: StateListener<Boolean>): DisposableHandle = synchronized(this) {
            used = true
            return currentCondition.invokeWhen(state, ignoreCurrent, listener)
        }

        /**
         * Shortcut for the or operator
         */
        operator fun plusAssign(condition: Condition) {
            synchronized(this) {
                if (used) throw IllegalStateException("Cannot add condition once a listener has been added")
                currentCondition = currentCondition or condition
            }
        }
    }

    protected operator fun Subsystem.unaryPlus() = (requiredSubsystems as MutableList).add(this)

    open suspend fun initialize0() {
        (commandState as CommandStateImpl).changeValue(CommandState.BAKING)
        initialize()
        timeoutCondition?.start(startTime)
    }

    open suspend fun execute0() = execute()
    open suspend fun dispose0() {
        timeoutCondition?.stop()
        dispose()
        (commandState as CommandStateImpl).changeValue(CommandState.BAKED)
    }

    protected open suspend fun initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start(): Deferred<Unit> {
        if(commandState.value == CommandState.BAKING){
            println("[Command] ${this::class.java.simpleName} is already running, discarding start.")
            return CompletableDeferred(Unit)
        }
        if(commandState.value == CommandState.QUEUED){
            println("[Command] ${this::class.java.simpleName} is already queued, discarding start.")
            return CompletableDeferred(Unit)
        }
        (commandState as CommandStateImpl).changeValue(CommandState.QUEUED)
        return CommandHandler.start(this, System.nanoTime())
    }
    fun stop() = CommandHandler.stop(this, System.nanoTime())

    fun withExit(condition: Condition) = also { finishCondition += condition }
    fun withTimeout(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) = also {
        timeout = delay to unit
        if (timeoutCondition == null) {
            timeoutCondition = DelayCondition(delay, unit)
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


    private inner class CommandStateImpl : StateImpl<CommandState>(CommandState.PREPARED) {
        fun changeValue(value: CommandState) {
            this.internalValue = value
        }
    }

}
