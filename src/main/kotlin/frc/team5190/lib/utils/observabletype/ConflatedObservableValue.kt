package frc.team5190.lib.utils.observabletype

import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlinx.coroutines.experimental.channels.ConflatedBroadcastChannel
import kotlinx.coroutines.experimental.channels.ReceiveChannel
import kotlinx.coroutines.experimental.channels.sendBlocking
import kotlinx.coroutines.experimental.launch
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.coroutines.experimental.CoroutineContext

interface ConflatedObservableValue<T> : ObservableValue<T> {
    val parent: ObservableValue<T>
    val context: CoroutineContext

    companion object {
        operator fun <T> invoke(
                context: CoroutineContext = DefaultDispatcher,
                parent: ObservableValue<T>
        ): ConflatedObservableValue<T> = ConflatedObservableValueImpl(context, parent)
    }
}

private class ConflatedObservableValueImpl<T>(
        override val context: CoroutineContext,
        override val parent: ObservableValue<T>
) : ConflatedObservableValue<T>, AbstractObservableValueImpl<T>() {

    private val broadcastChannel = ConflatedBroadcastChannel<T>()

    override val value: T
        get() {
            if (subscriptions.isEmpty()) {
                val newValue = parent.value
                broadcastChannel.sendBlocking(newValue)
                return newValue
            }
            return broadcastChannel.value
        }

    private lateinit var handle: ObservableHandle

    override fun start() {
        handle = parent.invokeWhen {
            broadcastChannel.sendBlocking(it)
        }
    }

    override fun stop() {
        handle.dispose()
    }

    private val subscriptions = CopyOnWriteArrayList<ReceiveChannel<T>>()

    override fun invokeOnSet(listener: ObservableListener<T>): ObservableHandle {
        val channel = broadcastChannel.openSubscription()

        synchronized(running) {
            val modifiedChannel = object : ReceiveChannel<T> by channel {
                override fun cancel(cause: Throwable?): Boolean {
                    synchronized(running) {
                        subscriptions -= this
                        if (subscriptions.isEmpty()) stop0()
                    }
                    return channel.cancel(cause)
                }
            }
            val handle = object : ObservableHandle {
                override fun dispose() {
                    modifiedChannel.cancel()
                }
            }
            launch(context) {
                for (newValue in modifiedChannel) {
                    listener(handle, newValue)
                }
            }
            subscriptions += modifiedChannel
            start0()
            return handle
        }
    }

}