package org.ghrobotics.lib.utils.observabletype

import kotlinx.coroutines.experimental.CoroutineScope
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.channels.Channel
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.launch

fun <T> ObservableValue<T>.asConflated(scope: CoroutineScope): ObservableValue<T> =
        ConflatedObservableValueImpl(scope, this)

private class ConflatedObservableValueImpl<T>(
        val scope: CoroutineScope,
        val parent: ObservableValue<T>
) : ObservableValue<T>, SubscribableObservableValueImpl<T>() {

    override var value: T = parent.value
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            val newValue = parent.value
            value = newValue
            return newValue
        }

    private val buffer = Channel<T>(Channel.CONFLATED)

    private lateinit var handle: ObservableHandle
    private lateinit var job: Job

    override fun start() {
        handle = parent.invokeOnSet {
            buffer.sendBlocking(it)
        }
        job = scope.launch {
            for (event in buffer)
                value = event
        }
    }

    override fun stop() {
        handle.dispose()
        job.cancel()
    }

    override fun toString() = "Conflated($parent)[$value]"

}