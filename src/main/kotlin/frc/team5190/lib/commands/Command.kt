package frc.team5190.lib.commands

import frc.team5190.lib.utils.launchFrequency
import frc.team5190.lib.utils.observabletype.ObservableValue
import frc.team5190.lib.utils.observabletype.ObservableValueReference
import frc.team5190.lib.utils.observabletype.ObservableVariable
import frc.team5190.lib.utils.observabletype.or
import frc.team5190.lib.wrappers.FalconRobotBase
import kotlinx.coroutines.experimental.*
import java.util.concurrent.TimeUnit

abstract class Command(val requiredSubsystems: List<Subsystem>) {
    companion object {
        const val DEFAULT_FREQUENCY = 50

        protected val commandContext = newFixedThreadPoolContext(2, "Command")
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
        internal set

    /**
     * Is true when all the finish conditions are met
     */
    fun isFinished() = finishCondition.value

    private var executor: Job? = null

    open suspend fun initialize0() {
        _commandState.value = CommandState.BAKING
        initialize()
        _timeoutCondition?.start(startTime)
        if(!finishCondition.value) {
            if (executeFrequency != 0) executor = launchFrequency(executeFrequency, commandContext) {
                execute0()
            }
        }
    }

    protected open suspend fun execute0() = execute()

    open suspend fun dispose0() {
        executor?.cancelAndJoin()
        executor = null
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

    suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        cont.disposeOnCancellation(_commandState.invokeOnceWhen(CommandState.BAKED) {
            cont.resume(Unit)
        })
    }

    protected class FinishCondition private constructor(private val varReference: ObservableValueReference<Boolean>) : ObservableValue<Boolean> by varReference {
        constructor() : this(ObservableValueReference(ObservableValue(false)))

        operator fun plusAssign(other: ObservableValue<Boolean>) {
            varReference.reference = varReference.reference or other
        }

        fun set(other: ObservableValue<Boolean>) {
            varReference.reference = other
        }
    }

}
