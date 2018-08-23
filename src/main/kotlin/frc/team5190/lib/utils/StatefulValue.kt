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

interface StatefulValue<T> {
    val value: T

    fun openSubscription(context: CoroutineContext = stateContext): ReceiveChannel<T>
    fun openSubscription(context: CoroutineContext = stateContext, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle {
        val channel = openSubscription(context)
        return block(channel)
    }

    fun invokeWhen(state: T, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeWhen(listOf(state), context, listener)

    fun invokeWhen(states: Collection<T>, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeOnChange(context, true) { if (states.contains(it)) listener(this, it) }

    fun invokeOnChangeTo(state: T, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeOnChangeTo(listOf(state), context, listener)

    fun invokeOnChangeTo(states: Collection<T>, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeOnChange(context, false) { if (states.contains(it)) listener(this, it) }

    fun invokeOnceOnChange(context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeOnChange(context) {
                listener(this, it)
                dispose()
            }

    fun invokeOnceWhen(state: T, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeOnceWhen(listOf(state), context, listener)

    fun invokeOnceWhen(states: Collection<T>, context: CoroutineContext = stateContext, listener: StateListener<T>) =
            invokeWhen(states, context) {
                listener(this, it)
                dispose()
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

    fun asSource() = Source { value }

    companion object {
        fun <T> of(value: T) = object : StatefulConstant<T> {
            override val value = value
        }
    }

}

@Suppress("FunctionName")
fun <T> StatefulValue(value: T) = StatefulValue.of(value)

@Deprecated("", ReplaceWith("StatefulValue(value)"))
fun <T> constState(value: T): StatefulValue<T> = StatefulValue(value)

interface StatefulConstant<T> : StatefulValue<T> {
    override fun openSubscription(context: CoroutineContext, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle = NonDisposableHandle
    override fun openSubscription(context: CoroutineContext): ReceiveChannel<T> = TODO("Constant states cannot be subscribed to")
}

abstract class StatefulValueImpl<T>(initValue: T) : StatefulValue<T> {
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

// Merging states

fun <F, T> processedState(state: StatefulValue<F>, processing: (F) -> T) =
        processedState(listOf(state)) { values -> processing(values.first()) }

fun <F1, F2, T> processedState(one: StatefulValue<F1>, two: StatefulValue<F2>, processing: (F1, F2) -> T) =
        processedState(listOf(one, two)) { values ->
            @Suppress("UNCHECKED_CAST")
            processing(values[0] as F1, values[1] as F2)
        }

fun <F, T> processedState(states: List<StatefulValue<out F>>, processing: (List<F>) -> T): StatefulValue<T> =
        object : StatefulValueImpl<T>(processing(states.map { it.value })) {
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

typealias StateListener<T> = DisposableHandle.(T) -> Unit

// Comparision State

fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThan(other: T) = greaterThan(StatefulValue(other))
fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThan(other: StatefulValue<T>) = compareToInternal(other) { it > 0 }

fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThan(other: T) = lessThan(StatefulValue(other))
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThan(other: StatefulValue<T>) = compareToInternal(other) { it < 0 }

fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThanOrEquals(other: T) = greaterThanOrEquals(StatefulValue(other))
fun <F : Comparable<T>, T : Any> StatefulValue<F>.greaterThanOrEquals(other: StatefulValue<T>) = compareToInternal(other) { it >= 0 }

fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThanOrEquals(other: T) = lessThanOrEquals(StatefulValue(other))
fun <F : Comparable<T>, T : Any> StatefulValue<F>.lessThanOrEquals(other: StatefulValue<T>) = compareToInternal(other) { it <= 0 }

fun <F : Comparable<T>, T : Any> StatefulValue<F>.compareTo(other: T) = compareTo(StatefulValue(other))
fun <F : Comparable<T>, T : Any> StatefulValue<F>.compareTo(other: StatefulValue<T>) = compareToInternal(other) { it }

private fun <F : Comparable<T>, T : Any, E> StatefulValue<F>.compareToInternal(other: StatefulValue<T>, block: (Int) -> E) =
        processedState(this, other) { one, two -> block(one.compareTo(two)) }

fun <F> comparisionState(one: StatefulValue<out F>, two: StatefulValue<out F>, processing: (F, F) -> Boolean): BooleanState =
        processedState(listOf(one, two)) { values -> processing(values[0], values[1]) }

// Variable State

fun <T> variableState(initValue: T): StatefulVariable<T> =
        VariableStateImpl(initValue)

private class VariableStateImpl<T>(initValue: T) : StatefulValueImpl<T>(initValue), StatefulVariable<T> {
    override var value: T
        set(value) {
            changeValue(value)
        }
        get() = super.value
}

interface StatefulVariable<T> : StatefulValue<T> {
    override var value: T
}

// Updatable State

fun <T> updatableState(frequency: Int = 50, block: () -> T): StatefulValue<T> =
        UpdatableStatefulValue(frequency, block)

private class UpdatableStatefulValue<T>(private val frequency: Int = 50, private val block: () -> T) : StatefulValueImpl<T>(block()) {
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

typealias BooleanState = StatefulValue<Boolean>
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

operator fun <T, V> Map<T, V>.get(key: StatefulValue<T>): StatefulValue<V?> =
        processedState(key) { this@get[it] }

// Sensor Extensions

val AnalogInput.voltageState
    get() = voltageState()

fun AnalogInput.voltageState(frequency: Int = 50) =
        updatableState(frequency) { this@voltageState.averageVoltage }