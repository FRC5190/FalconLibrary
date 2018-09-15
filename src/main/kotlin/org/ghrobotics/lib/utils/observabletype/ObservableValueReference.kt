package org.ghrobotics.lib.utils.observabletype

interface ObservableValueReference<T> : ObservableValue<T> {
    var reference: ObservableValue<T>

    companion object {
        operator fun <T> invoke(reference: ObservableValue<T>): ObservableValueReference<T> = ObservableValueReferenceImpl(reference)
    }
}

private class ObservableValueReferenceImpl<T>(reference: ObservableValue<T>) : ObservableValueReference<T>, SubscribableObservableValueImpl<T>() {

    private val referenceSync = Any()

    override var reference = reference
        set(value) = synchronized(referenceSync) {
            field = value
            synchronized(running) {
                if (running.get()) {
                    synchronized(referenceSync) {
                        start()
                        stop()
                    }
                }
            }
        }

    override var value: T = reference.value
        set(value) {
            informListeners(value)
            field = value
        }

    private lateinit var handle: ObservableHandle

    override fun start() = synchronized(referenceSync) {
        handle = reference.invokeWhen { value = it }
    }

    override fun stop() = synchronized(referenceSync) {
        handle.dispose()
    }


}