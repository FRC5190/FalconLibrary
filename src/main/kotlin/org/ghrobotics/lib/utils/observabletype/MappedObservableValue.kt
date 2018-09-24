package org.ghrobotics.lib.utils.observabletype

interface MappedObservableValue<V, F> : ObservableValue<F> {
    val parent: ObservableValue<V>
    val mapper: (V) -> F

    companion object {
        operator fun <V, F> invoke(
                parent: ObservableValue<V>,
                mapper: (V) -> F
        ): MappedObservableValue<V, F> = MappedObservableValueImpl(parent, mapper)
    }
}

private class MappedObservableValueImpl<V, F>(
        override val parent: ObservableValue<V>,
        override val mapper: (V) -> F
) : MappedObservableValue<V, F>, SubscribableObservableValueImpl<F>() {

    override var value: F = mapper(parent.value)
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            synchronized(running) {
                if (!running.get()) value = mapper(parent.value)
            }
            return field
        }

    private lateinit var handle: ObservableHandle

    override fun start() {
        handle = parent.invokeWhen { value = mapper(it) }
    }

    override fun stop() {
        handle.dispose()
    }

    override fun toString() = "MAPPED($parent)[$value]"

}