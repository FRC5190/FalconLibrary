package frc.team5190.lib.utils.observabletype

import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

internal abstract class AbstractObservableValueImpl<T> : ObservableValue<T> {

    protected val running = AtomicBoolean(false)

    protected fun start0() = synchronized(running) {
        if (!running.compareAndSet(false, true)) return
        start()
    }

    protected fun stop0() = synchronized(running) {
        if (!running.compareAndSet(true, false)) return
        stop()
    }

    open fun start() {}
    open fun stop() {}

}

internal abstract class SubscribableObservableValueImpl<T> : AbstractObservableValueImpl<T>() {

    private val listeners = CopyOnWriteArrayList<ListenerEntry>()

    protected fun informListeners(newValue: T) {
        listeners.forEach { it.listener(it, newValue) }
    }

    override fun invokeOnSet(listener: ObservableListener<T>): ObservableHandle {
        val entry = ListenerEntry(listener)
        synchronized(running) {
            start0()
            listeners.add(entry)
        }
        return entry
    }

    protected inner class ListenerEntry(val listener: ObservableListener<T>) : ObservableHandle {
        override fun dispose() {
            synchronized(running) {
                listeners.remove(this)
                if (listeners.isEmpty()) stop0()
            }
        }
    }

}