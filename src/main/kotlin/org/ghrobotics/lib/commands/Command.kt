package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.*
import org.ghrobotics.lib.utils.loopFrequency
import org.ghrobotics.lib.utils.observabletype.*
import org.ghrobotics.lib.wrappers.FalconRobotBase
import java.util.concurrent.TimeUnit

abstract class Command(val requiredSubsystems: List<Subsystem>) {
    companion object {
        const val DEFAULT_FREQUENCY = 50

        protected val commandScope = CoroutineScope(newFixedThreadPoolContext(2, "Command"))
    }

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[Command} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
    }

    constructor(vararg requiredSubsystems: Subsystem) : this(requiredSubsystems.toList())

    @Suppress("PropertyName")
    protected val _finishCondition = FinishCondition()
    val finishCondition: ObservableValue<Boolean> = _finishCondition

    var executeFrequency = DEFAULT_FREQUENCY
        protected set

    private var _timeoutCondition: StatefulDelayImpl? = null
    internal val timeoutCondition: StatefulDelay?
        get() = _timeoutCondition

    private val _commandState = ObservableVariable(CommandState.PREPARED)
    val commandState: ObservableValue<CommandState> = _commandState

    var startTime = 0L
        private set

    /**
     * Is true when all the finish conditions are met
     */
    fun isFinished() = finishCondition.value

    // Internal Task of the Command

    private var internalJob: Job? = null
    private var finishHandle: DisposableHandle? = null

    internal suspend fun internalStart(startTime: Long, onFinish: (Long) -> Unit) {
        this.startTime = startTime
        _commandState.value = CommandState.BAKING
        internalJob = commandScope.launch {
            initialize0()
            // Only start if the command didn't end already
            if (!finishCondition.value) {
                finishHandle = finishCondition.invokeOnceWhenTrue {
                    val timeoutCondition = this@Command._timeoutCondition

                    val stopTime = if (timeoutCondition != null)
                        Math.min(System.nanoTime(), startTime + timeoutCondition.unit.toNanos(timeoutCondition.delay))
                    else System.nanoTime()

                    onFinish(stopTime)
                }
                if (executeFrequency != 0) loopFrequency(executeFrequency) { execute0() }
            } else {
                onFinish(startTime)
            }
        }
    }

    internal suspend fun internalStop() {
        assert(internalJob != null) { "Tried to stop a command that isnt running!" }
        internalJob?.cancelAndJoin()
        internalJob = null
        finishHandle?.dispose()
        finishHandle = null
        dispose0()
        _commandState.value = CommandState.BAKED
    }

    // Some methods for people who know what they are doing

    open suspend fun initialize0() {
        initialize()
        _timeoutCondition?.start(startTime)
    }

    protected open suspend fun execute0() = execute()

    open suspend fun dispose0() {
        _timeoutCondition?.stop()
        dispose()
    }

    // Methods that don't need anything special

    protected open suspend fun initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start(): Deferred<Unit> {
        if (_commandState.value == CommandState.BAKING) {
            println("[Command] ${this::class.java.simpleName} is already running, discarding start.")
            return CompletableDeferred(Unit)
        }
        if (_commandState.value == CommandState.QUEUED) {
            println("[Command] ${this::class.java.simpleName} is already queued, discarding start.")
            return CompletableDeferred(Unit)
        }
        _commandState.value = CommandState.QUEUED
        return CommandHandler.start(this, System.nanoTime())
    }

    fun stop() = CommandHandler.stop(this, System.nanoTime())

    fun withExit(condition: ObservableValue<Boolean>) = also { _finishCondition += condition }
    fun overrideExit(condition: ObservableValue<Boolean>) = also { _finishCondition.set(condition) }
    fun withTimeout(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) = also {
        if (_timeoutCondition == null) {
            _timeoutCondition = StatefulDelayImpl(delay, unit)
            _finishCondition += _timeoutCondition!!
        } else {
            _timeoutCondition!!.delay = delay
            _timeoutCondition!!.unit = unit
        }
    }

    fun withTimeoutSeconds(delaySeconds: Double) = withTimeout((delaySeconds * 1000).toLong(), TimeUnit.MILLISECONDS)

    suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        cont.disposeOnCancellation(_commandState.invokeOnceWhen(CommandState.BAKED) {
            cont.resume(Unit)
        })
    }

    protected class FinishCondition private constructor(private val varReference: ObservableValueReference<Boolean>) :
        ObservableValue<Boolean> by varReference {
        constructor() : this(ObservableValueReference(ObservableValue(false)))

        operator fun plusAssign(other: ObservableValue<Boolean>) {
            varReference.reference = varReference.reference or other
        }

        fun set(other: ObservableValue<Boolean>) {
            varReference.reference = other
        }

        override fun toString() = "FINISH($varReference)[$value]"
    }

}
