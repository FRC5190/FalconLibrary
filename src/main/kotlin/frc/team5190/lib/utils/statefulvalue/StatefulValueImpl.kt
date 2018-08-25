package frc.team5190.lib.utils.statefulvalue

import frc.team5190.lib.utils.disposableHandle
import frc.team5190.lib.utils.launchFrequency
import kotlinx.coroutines.experimental.DisposableHandle
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.sendBlocking
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

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

class StatefulVariableImpl<T>(initValue: T) : StatefulValueImpl<T>(initValue), StatefulVariable<T> {
    override var value: T
        set(value) {
            changeValue(value)
        }
        get() = super.value
}

class StatefulUpdatableValue<T>(private val frequency: Int,
                                         private val block: () -> T) : StatefulValueImpl<T>(block()) {
    private lateinit var job: Job

    private val timeSync = Any()
    private var lastUpdate = System.nanoTime()
    private val timeBetweenUpdate = TimeUnit.SECONDS.toNanos(1) / frequency

    override val value: T
        get() = synchronized(listenSync) {
            if (!listenActive) synchronized(timeSync) {
                // Ensure it doesn't update faster then the specified frequency
                val currentTime = System.nanoTime()
                if (currentTime - lastUpdate >= timeBetweenUpdate) {
                    lastUpdate += timeBetweenUpdate
                    changeValue(block())
                }
            }
            super.value
        }

    override fun initWhenUsed(context: CoroutineContext) {
        job = launchFrequency(frequency, context) {
            synchronized(timeSync) {
                lastUpdate = System.nanoTime()
                changeValue(block())
            }
        }
    }

    override fun disposeWhenUnused() {
        job.cancel()
    }
}

class StatefulMergedValueImpl<V, T>(private val states: List<StatefulValue<out V>>,
                                    private val processing: (List<V>) -> T) : StatefulValueImpl<T>(processing(states.map { it.value })) {

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