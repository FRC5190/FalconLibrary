@file:Suppress("FunctionName")

package frc.team5190.lib.utils.statefulvalue

import frc.team5190.lib.utils.Source
import frc.team5190.lib.utils.disposableHandle
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlin.coroutines.experimental.CoroutineContext

// Basic Implementation

internal val STATEFUL_CONTEXT = newFixedThreadPoolContext(2, "State Context")
private const val DEFAULT_STATE_FREQUENCY = 50

typealias StatefulListener<T> = DisposableHandle.(T) -> Unit

interface StatefulValue<T> {
    val value: T

    fun openSubscription(context: CoroutineContext = STATEFUL_CONTEXT): ReceiveChannel<T>
    fun openSubscription(context: CoroutineContext = STATEFUL_CONTEXT, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle {
        val channel = openSubscription(context)
        return block(channel)
    }

    fun invokeWhen(state: T, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeWhen(listOf(state), context, listener)

    fun invokeWhen(states: Collection<T>, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnChange(context, true) { if (states.contains(it)) listener(this, it) }

    fun invokeOnChangeTo(state: T, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnChangeTo(listOf(state), context, listener)

    fun invokeOnChangeTo(states: Collection<T>, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnChange(context, false) { if (states.contains(it)) listener(this, it) }

    // Invoke Once Listeners

    fun invokeOnceOnChange(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnChange(context) {
                listener(this, it)
                dispose()
            }

    fun invokeOnceWhen(state: T, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnceWhen(listOf(state), context, listener)

    fun invokeOnceWhen(states: Collection<T>, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeWhen(states, context) {
                listener(this, it)
                dispose()
            }

    fun invokeOnceOnChangeTo(state: T, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnceOnChangeTo(listOf(state), context, listener)

    fun invokeOnceOnChangeTo(states: Collection<T>, context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulListener<T>) =
            invokeOnChangeTo(states, context) {
                listener(this, it)
                dispose()
            }

    fun invokeOnChange(context: CoroutineContext = STATEFUL_CONTEXT,
                       invokeFirst: Boolean = false,
                       listener: StatefulListener<T>) = openSubscription(context) { subscription ->
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

    fun <O : Comparable<T>> greaterThan(other: O): StatefulValue<Boolean> = greaterThan(other) { one, two -> -two.compareTo(one) }
    fun <O : Comparable<T>> lessThan(other: O): StatefulValue<Boolean> = lessThan(other) { one, two -> -two.compareTo(one) }
    fun <O : Comparable<T>> greaterThanOrEquals(other: O): StatefulValue<Boolean> = greaterThanOrEquals(other) { one, two -> -two.compareTo(one) }
    fun <O : Comparable<T>> lessThanOrEquals(other: O): StatefulValue<Boolean> = lessThanOrEquals(other) { one, two -> -two.compareTo(one) }
    fun <O : Comparable<T>> compareTo(other: O): StatefulValue<Int> = compareTo(other) { one, two -> -two.compareTo(one) }

    fun <O> greaterThan(other: O, compareTo: (T, O) -> Int): StatefulValue<Boolean> = greaterThan(StatefulValue(other), compareTo)
    fun <O> lessThan(other: O, compareTo: (T, O) -> Int): StatefulValue<Boolean> = lessThan(StatefulValue(other), compareTo)
    fun <O> greaterThanOrEquals(other: O, compareTo: (T, O) -> Int): StatefulValue<Boolean> = greaterThanOrEquals(StatefulValue(other), compareTo)
    fun <O> lessThanOrEquals(other: O, compareTo: (T, O) -> Int): StatefulValue<Boolean> = lessThanOrEquals(StatefulValue(other), compareTo)
    fun <O> compareTo(other: O, compareTo: (T, O) -> Int): StatefulValue<Int> = compareTo(StatefulValue(other), compareTo)

    fun <O> greaterThan(other: StatefulValue<O>, compareTo: (T, O) -> Int): StatefulValue<Boolean> = compareToInternal(other, compareTo) { it > 0 }
    fun <O> lessThan(other: StatefulValue<O>, compareTo: (T, O) -> Int): StatefulValue<Boolean> = compareToInternal(other, compareTo) { it < 0 }
    fun <O> greaterThanOrEquals(other: StatefulValue<O>, compareTo: (T, O) -> Int): StatefulValue<Boolean> = compareToInternal(other, compareTo) { it >= 0 }
    fun <O> lessThanOrEquals(other: StatefulValue<O>, compareTo: (T, O) -> Int): StatefulValue<Boolean> = compareToInternal(other, compareTo) { it <= 0 }
    fun <O> compareTo(other: StatefulValue<O>, compareTo: (T, O) -> Int): StatefulValue<Int> = compareToInternal(other, compareTo) { it }

    private fun <O, R> compareToInternal(other: StatefulValue<O>, compareTo: (T, O) -> Int, block: (Int) -> R): StatefulValue<R> =
            of(this, other) { one, two -> block(compareTo(one, two)) }

    fun <R> withProcessing(block: (T) -> R) = of(listOf(this)) { block(it[0]) }

    fun asSource() = Source { value }

    companion object {
        fun <T> of(value: T): StatefulValue<T> = object : StatefulConstant<T> {
            override val value = value
        }

        fun <T> of(frequency: Int = DEFAULT_STATE_FREQUENCY, value: () -> T): StatefulValue<T> = StatefulUpdatableValue(frequency, value)

        fun <T1, R> of(value1: StatefulValue<T1>, block: (T1) -> R): StatefulValue<R> = of(listOf(value1)) { block(it[0]) }
        fun <T1, T2, R> of(value1: StatefulValue<T1>, value2: StatefulValue<T2>, block: (T1, T2) -> R): StatefulValue<R> =
                of(listOf(value1, value2)) {
                    @kotlin.Suppress("UNCHECKED_CAST")
                    block(it[0] as T1, it[1] as T2)
                }

        fun <T1, T2, T3, R> of(value1: StatefulValue<T1>, value2: StatefulValue<T2>, value3: StatefulValue<T3>, block: (T1, T2, T3) -> R): StatefulValue<R> =
                of(listOf(value1, value2, value3)) {
                    @Suppress("UNCHECKED_CAST")
                    block(it[0] as T1, it[1] as T2, it[2] as T3)
                }

        fun <O, R> of(vararg others: StatefulValue<out O>, block: (List<O>) -> R): StatefulValue<R> = of(others.toList(), block)
        fun <O, R> of(others: List<StatefulValue<out O>>, block: (List<O>) -> R): StatefulValue<R> = when (others.size) {
            0 -> throw IllegalArgumentException("Cannot process without anything to process")
            1 -> StatefulProcessedValueImpl(others.first()) { block(listOf(it)) }
            else -> StatefulMergedValueImpl(others, block)
        }
    }
}

interface StatefulConstant<T> : StatefulValue<T> {
    override fun openSubscription(context: CoroutineContext, block: (ReceiveChannel<T>) -> DisposableHandle): DisposableHandle = NonDisposableHandle
    override fun openSubscription(context: CoroutineContext): ReceiveChannel<T> {
        TODO("Constant states cannot be subscribed to")
    }

    override fun invokeOnChange(context: CoroutineContext, invokeFirst: Boolean, listener: StatefulListener<T>): DisposableHandle {
        val handle = NonDisposableHandle
        if (invokeFirst) listener(handle, value)
        return handle
    }
}

interface StatefulVariable<T> : StatefulValue<T> {
    override var value: T

    companion object {
        fun <T> of(value: T): StatefulVariable<T> = StatefulVariableImpl(value)
    }
}

fun <T> StatefulValue(value: T): StatefulValue<T> = StatefulValue.of(value)

fun <T> StatefulValue(value: () -> T): StatefulValue<T> = StatefulValue.of(DEFAULT_STATE_FREQUENCY, value)
fun <T> StatefulValue(frequency: Int, value: () -> T): StatefulValue<T> = StatefulValue.of(frequency, value)

fun <T1, R> StatefulValue(
        value: StatefulValue<T1>,
        block: (T1) -> R
): StatefulValue<R> = StatefulValue.of(value, block)

fun <T1, T2, R> StatefulValue(
        value1: StatefulValue<T1>,
        value2: StatefulValue<T2>,
        block: (T1, T2) -> R
): StatefulValue<R> = StatefulValue.of(value1, value2, block)

fun <T1, T2, T3, R> StatefulValue(
        value1: StatefulValue<T1>,
        value2: StatefulValue<T2>,
        value3: StatefulValue<T3>,
        block: (T1, T2, T3) -> R
): StatefulValue<R> = StatefulValue.of(value1, value2, value3, block)

fun <T, R> StatefulValue(
        values: List<StatefulValue<out T>>,
        block: (List<T>) -> R
): StatefulValue<R> = StatefulValue.of(values, block)

fun <T> StatefulVariable(value: T): StatefulVariable<T> = StatefulVariable.of(value)


@Deprecated("", ReplaceWith("StatefulValue(value)"))
fun <T> constState(value: T): StatefulValue<T> = StatefulValue(value)

@Deprecated("", ReplaceWith("StatefulVariable(initValue)"))
fun <T> variableState(initValue: T): StatefulVariable<T> = StatefulVariable(initValue)

@Deprecated("", ReplaceWith("StatefulValue(frequency, block)"))
fun <T> updatableState(frequency: Int = 50, block: () -> T): StatefulValue<T> = StatefulValue(frequency, block)

@Deprecated("", ReplaceWith("StatefulValue(state, processing)"))
fun <F, T> processedState(state: StatefulValue<F>, processing: (F) -> T) = StatefulValue(state, processing)

@Deprecated("", ReplaceWith("StatefulValue(one, two, processing)"))
fun <F1, F2, T> processedState(one: StatefulValue<F1>, two: StatefulValue<F2>, processing: (F1, F2) -> T) = StatefulValue(one, two, processing)

@Deprecated("", ReplaceWith("StatefulValue(states, processing)"))
fun <F, T> processedState(states: List<StatefulValue<out F>>, processing: (List<F>) -> T): StatefulValue<T> = StatefulValue(states, processing)

@Deprecated("", ReplaceWith("StatefulValue(one, two, processing)"))
fun <F> comparisionState(one: StatefulValue<out F>, two: StatefulValue<out F>, processing: (F, F) -> Boolean): StatefulBoolean =
        StatefulValue(one, two, processing)
