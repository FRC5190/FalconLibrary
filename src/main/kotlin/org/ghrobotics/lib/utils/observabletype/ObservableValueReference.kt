package org.ghrobotics.lib.utils.observabletype

@Suppress("FunctionName")
fun <T> ObservableValueReference(reference: ObservableValue<T>): ObservableValueReference<T> =
        ObservableValueReferenceImpl(reference)

interface ObservableValueReference<T> : ObservableValue<T> {
    var reference: ObservableValue<T>
}

private class ObservableValueReferenceImpl<T>(reference: ObservableValue<T>) : ObservableValueReference<T>,
        SubscribableObservableValueImpl<T>() {

    private var handle: ObservableHandle? = null

    override var reference: ObservableValue<T> = reference
        set(value) {
            field = value
            synchronized(running) {
                if (handle != null) {
                    stop()
                    start()
                }
            }
        }

    override var value: T = reference.value
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            val newValue = reference.value
            value = newValue
            return newValue
        }

    override fun start() {
        handle = reference.invokeWhen { value = it }
    }

    override fun stop() {
        handle!!.dispose()
        handle = null
    }

    override fun toString() = "VALREF($reference)"

}