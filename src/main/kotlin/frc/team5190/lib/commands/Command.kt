package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.*
import frc.team5190.lib.wrappers.FalconRobotBase
import kotlinx.coroutines.experimental.CompletableDeferred
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.disposeOnCancellation
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit

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

    @Suppress("PropertyName")
    protected val _finishCondition = FinishCondition()
    val finishCondition: StatefulBoolean = _finishCondition

    var executeFrequency = DEFAULT_FREQUENCY
        protected set

    private var _timeoutCondition: StatefulDelayImpl? = null
    internal val timeoutCondition: StatefulDelay?
        get() = _timeoutCondition

    private val _commandState = StatefulVariable(CommandState.PREPARED)
    val commandState: StatefulValue<CommandState> = _commandState

    var startTime = 0L
        internal set

    /**
     * Is true when all the finish conditions are met
     */
    fun isFinished() = finishCondition.value

    open suspend fun initialize0() {
        _commandState.value = CommandState.BAKING
        initialize()
        _timeoutCondition?.start(startTime)
    }

    open suspend fun execute0() = execute()
    open suspend fun dispose0() {
        _timeoutCondition?.stop()
        dispose()
        _commandState.value = CommandState.BAKED
    }

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

    fun withExit(condition: StatefulBoolean) = also { _finishCondition += condition }
    fun withTimeout(delay: Long, unit: TimeUnit = TimeUnit.MILLISECONDS) = also {
        if (_timeoutCondition == null) {
            _timeoutCondition = StatefulDelayImpl(delay, unit)
            _finishCondition += _timeoutCondition!!
        } else {
            _timeoutCondition!!.delay = delay
            _timeoutCondition!!.unit = unit
        }
    }

    suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        cont.disposeOnCancellation(_commandState.invokeOnceWhenFinished {
            cont.resume(Unit)
        })
    }

    protected class FinishCondition private constructor(private val varReference: StatefulVariableReference<Boolean>) : StatefulBoolean by varReference {
        constructor() : this(StatefulVariableReference(StatefulValue(false)))

        operator fun plusAssign(other: StatefulBoolean) {
            varReference.reference = varReference.reference or other
        }
    }

}
