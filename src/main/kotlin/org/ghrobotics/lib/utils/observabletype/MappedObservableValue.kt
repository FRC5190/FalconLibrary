package org.ghrobotics.lib.utils.observabletype

fun <F, T> ObservableValue<F>.map(block: (F) -> T): ObservableValue<T> =
    MappedObservableValueImpl(this, block)

private class MappedObservableValueImpl<F, T>(
    val parent: ObservableValue<F>,
    val mapper: (F) -> T
) : ObservableValue<T>, SubscribableObservableValueImpl<T>() {

    override var value: T = mapper(parent.value)
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            val newValue = mapper(parent.value)
            field = newValue
            return newValue
        }

    private lateinit var handle: ObservableHandle

    override fun start() {
        handle = parent.invokeOnSet { value = mapper(it) }
    }

    override fun stop() {
        handle.dispose()
    }

    override fun toString() = "MAPPED($parent)[$value]"

}