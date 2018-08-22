package frc.team5190.lib.utils

import edu.wpi.first.wpilibj.AnalogInput
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ChannelIterator
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlin.coroutines.experimental.CoroutineContext

// Basic Implementation

private val stateContext = newFixedThreadPoolContext(2, "State Context")

interface State<T> : Source<T> {
    fun openSubscription(context: CoroutineContext = stateContext): ReceiveChannel<T>
    fun openSubscription(context: CoroutineContext = stateContext, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle {
        val channel = openSubscription(context)
        return block(channel)
    }

    fun invokeOnChange(context: CoroutineContext = stateContext,
                       invokeFirst: Boolean = false,
                       listener: StateListener<T>) = openSubscription(context) { subscription ->
        var lastValue = value
        lateinit var job: Job
        val disposableHandle by lazy { disposableHandle { job.cancel() } }
        job = launch(context, start = CoroutineStart.LAZY) {
            for (evt in subscription) {
                if (evt == lastValue) continue
                listener(disposableHandle, evt)
                lastValue = evt
            }
        }
        if (invokeFirst) listener(disposableHandle, lastValue)
        job.start()
        job.invokeOnCompletion { subscription.cancel() }
        disposableHandle
    }
}

abstract class StateImpl<T>(initValue: T) : State<T> {
    private val changeSync = Any()
    protected var listenActive = false
        private set
    protected val listenSync = Any()
    private val channel = ConflatedBroadcastChannel<T>()

    init {
        channel.sendBlocking(initValue)
    }

    override val value
        get() = internalValue

    private val internalValue
        get() = channel.value

    protected open fun initWhenUsed(context: CoroutineContext) {}
    protected open fun disposeWhenUnused() {}

    override fun openSubscription(context: CoroutineContext): ReceiveChannel<T> {
        synchronized(listenSync) {
            if (!listenActive) {
                listenActive = true
                initWhenUsed(context)
            }
            val subscription = channel.openSubscription()
            return object : ReceiveChannel<T> by subscription {
                override fun cancel(cause: Throwable?): Boolean {
                    synchronized(listenSync) {
                        if (!listenActive) disposeWhenUnused()
                        listenActive = false
                    }
                    return subscription.cancel(cause)
                }
            }
        }
    }

    //    protected fun changeValue(newValue: T) = synchronized(changeSync) {
    //        if (internalValue != newValue)
    //            channel.offer(newValue)
    //    }
    protected fun changeValue(newValue: T) {
        channel.offer(newValue)
    }
}
// Invoke When Helpers

fun <T> State<T>.invokeWhen(
        state: T,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeWhen(listOf(state), context, listener)

fun <T> State<T>.invokeWhen(
        states: Collection<T>,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeOnChange(context, true) {
    if (states.contains(it)) listener(this, it)
}

// Invoke Change Helpers

fun <T> State<T>.invokeOnChangeTo(
        state: T,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeOnChangeTo(listOf(state), context, listener)

fun <T> State<T>.invokeOnChangeTo(
        states: Collection<T>,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeOnChange(context, false) {
    if (states.contains(it)) listener(this, it)
}

// Invoke Once Helpers

fun <T> State<T>.invokeOnceOnChange(
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeOnChange(context) {
    listener(this, it)
    dispose()
}

fun <T> State<T>.invokeOnceWhen(
        state: T,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeOnceWhen(listOf(state), context, listener)

fun <T> State<T>.invokeOnceWhen(
        states: Collection<T>,
        context: CoroutineContext = stateContext,
        listener: StateListener<T>
) = invokeWhen(states, context) {
    listener(this, it)
    dispose()
}

// Merging states

fun <F, T> processedState(state: State<F>, processing: (F) -> T) = processedState(listOf(state)) { values -> processing(values.first()) }

fun <F, T> processedState(states: List<State<out F>>, processing: (List<F>) -> T): State<T> =
        object : StateImpl<T>(processing(states.map { it.value })) {
            private var handle: DisposableHandle? = null

            override val value: T
                get() = synchronized(listenSync) {
                    if (!listenActive) changeValue(processing(states.map { it.value }))
                    super.value
                }

            override fun initWhenUsed(context: CoroutineContext) = synchronized(listenSync) {
                handle = disposableHandle(states.map { state ->
                    state.invokeOnChange(context) { value ->
                        val newValues = states.map { stateVal -> if (stateVal == state) value else stateVal.value }
                        synchronized(listenSync) {
                            changeValue(processing(newValues))
                        }
                    }
                })
                changeValue(processing(states.map { it.value }))
            }

            override fun disposeWhenUnused() {
                handle?.dispose()
                handle = null
            }
        }

fun <T> constState(value: T): State<T> = ConstantState(value)

class ConstantState<T>(override val value: T) : State<T> {
    override fun openSubscription(context: CoroutineContext, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle = NonDisposableHandle
    override fun openSubscription(context: CoroutineContext): ReceiveChannel<T> = TODO("Constant states cannot be subscribed to")
}

typealias StateListener<T> = DisposableHandle.(T) -> Unit

// Comparision State

fun <F> comparisionState(one: State<out F>, two: State<out F>, processing: (F, F) -> Boolean): BooleanState =
        processedState(listOf(one, two)) { values -> processing(values[0], values[1]) }

// Variable State

fun <T> variableState(initValue: T): VariableState<T> = VariableStateImpl(initValue)

private class VariableStateImpl<T>(initValue: T) : StateImpl<T>(initValue), VariableState<T> {
    override var value: T
        set(value) {
            changeValue(value)
        }
        get() = super.value
}

interface VariableState<T> : State<T> {
    override var value: T
}

// Updatable State

fun <T> updatableState(frequency: Int = 50, block: () -> T): StateImpl<T> = UpdatableState(frequency, block)

private class UpdatableState<T>(private val frequency: Int = 50, private val block: () -> T) : StateImpl<T>(block()) {
    private lateinit var job: Job

    override val value: T
        get() = synchronized(listenSync) {
            if (!listenActive) changeValue(block())
            super.value
        }

    override fun initWhenUsed(context: CoroutineContext) {
        job = launchFrequency(frequency, context) {
            changeValue(block())
        }
    }

    override fun disposeWhenUnused() {
        job.cancel()
    }
}

// Boolean State

typealias BooleanState = State<Boolean>
typealias BooleanListener = StateListener<Boolean>

fun BooleanState.invokeOnTrue(context: CoroutineContext = stateContext, listener: BooleanListener) = invokeOnChangeTo(true, context, listener)
fun BooleanState.invokeOnFalse(context: CoroutineContext = stateContext, listener: BooleanListener) = invokeOnChangeTo(false, context, listener)

fun BooleanState.invokeWhenTrue(context: CoroutineContext = stateContext, listener: BooleanListener) = invokeWhen(true, context, listener)
fun BooleanState.invokeWhenFalse(context: CoroutineContext = stateContext, listener: BooleanListener) = invokeWhen(false, context, listener)

operator fun BooleanState.not(): BooleanState = object : BooleanState {
    override val value: Boolean
        get() = !this@not.value

    override fun openSubscription(context: CoroutineContext): ReceiveChannel<Boolean> {
        val subscription = this@not.openSubscription(context)
        return object : ReceiveChannel<Boolean> by subscription {
            override fun iterator(): ChannelIterator<Boolean> {
                val iterator = subscription.iterator()
                return object : ChannelIterator<Boolean> by iterator {
                    override suspend fun next() = !iterator.next()
                }
            }
        }
    }
}

// Extensions

operator fun <T, V> Map<T, V>.get(key: State<T>): State<V?> = processedState(key) { this@get[it] }

// Sensor Extensions

val AnalogInput.voltageState
    get() = voltageState()

fun AnalogInput.voltageState(frequency: Int = 50) =
        updatableState(frequency) { this@voltageState.averageVoltage }