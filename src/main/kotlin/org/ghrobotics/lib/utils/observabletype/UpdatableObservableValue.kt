package org.ghrobotics.lib.utils.observabletype

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import org.ghrobotics.lib.utils.launchFrequency
import java.util.concurrent.TimeUnit

fun <T> CoroutineScope.updatableValue(
        frequency: Int = kDefaultUpdatableObservableValueReadTime,
        block: () -> T
): UpdatableObservableValue<T> = UpdatableObservableValueImpl(this, frequency, block)

interface UpdatableObservableValue<T> : ObservableValue<T> {
    val scope: CoroutineScope
    val frequency: Int
}

private class UpdatableObservableValueImpl<T>(
        override val scope: CoroutineScope,
        override val frequency: Int,
        private val block: () -> T
) : UpdatableObservableValue<T>, SubscribableObservableValueImpl<T>() {

    private val deltaTime = TimeUnit.SECONDS.toNanos(1) / frequency

    private var lastUpdateTime = System.nanoTime()

    override var value: T = block()
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            if (running.get())
                return field
            synchronized(running) {
                val currentTime = System.nanoTime()
                if (currentTime - lastUpdateTime < deltaTime)
                    return@synchronized // no need for update
                lastUpdateTime = currentTime
                value = block()
            }
            return field
        }

    private lateinit var job: Job

    override fun start() {
        job = scope.launchFrequency(frequency) {
            synchronized(running) {
                value = block()
                lastUpdateTime = System.nanoTime()
            }
        }
    }

    override fun stop() {
        job.cancel()
    }


    override fun toString() = "UPDATE[$value]"

}