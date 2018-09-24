package org.ghrobotics.lib.utils.observabletype

import org.ghrobotics.lib.utils.launchFrequency
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.Job
import java.util.concurrent.TimeUnit
import kotlin.coroutines.experimental.CoroutineContext

interface UpdatableObservableValue<T> : ObservableValue<T> {
    val context: CoroutineContext
    val frequency: Int

    companion object {
        operator fun <T> invoke(
                frequency: Int = kDefaultUpdatableObservableValueReadTime,
                context: CoroutineContext = DefaultDispatcher,
                block: () -> T
        ): UpdatableObservableValue<T> = UpdatableObservableValueImpl(context, frequency, block)
    }
}

private class UpdatableObservableValueImpl<T>(
        override val context: CoroutineContext,
        override val frequency: Int,
        private val block: () -> T
) : UpdatableObservableValue<T>, SubscribableObservableValueImpl<T>() {

    override var value: T = block()
        set(value) {
            informListeners(value)
            field = value
        }
        get() {
            synchronized(running) {
                if (!running.get()) {
                    synchronized(updateSync) {
                        val currentTime = System.nanoTime()
                        if (currentTime - lastUpdate > deltaTime) {
                            value = block()
                            lastUpdate = currentTime
                        }
                    }
                }
            }
            return field
        }

    private val updateSync = Any()
    private var lastUpdate = System.nanoTime()
    private val deltaTime = TimeUnit.SECONDS.toNanos(1) / frequency

    private lateinit var job: Job

    override fun start() {
        job = launchFrequency(frequency, context) {
            synchronized(updateSync) {
                value = block()
                lastUpdate = System.nanoTime()
            }
        }
    }

    override fun stop() {
        job.cancel()
    }

    override fun toString() = "UPDATE[$value]"

}