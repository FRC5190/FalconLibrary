package frc.team5190.lib.utils.observabletype

interface MergedObservableValue<V1, V2, F> : ObservableValue<F> {
    val first: ObservableValue<V1>
    val second: ObservableValue<V2>

    val merger: (V1, V2) -> F

    companion object {
        operator fun <V1, V2, F> invoke(
                first: ObservableValue<V1>,
                second: ObservableValue<V2>,
                merger: (V1, V2) -> F
        ): MergedObservableValue<V1, V2, F> = MergedObservableValueImpl(first, second, merger)
    }
}

private class MergedObservableValueImpl<V1, V2, F>(
        override val first: ObservableValue<V1>,
        override val second: ObservableValue<V2>,
        override val merger: (V1, V2) -> F
) : MergedObservableValue<V1, V2, F>, SubscribableObservableValueImpl<F>() {

    override var value: F = merger(first.value, second.value)
        private set(newValue) {
            informListeners(newValue)
            field = newValue
        }
        get() {
            synchronized(running) {
                if(!running.get()) value = merger(first.value, second.value)
            }
            return field
        }

    private lateinit var handle: ObservableHandle

    override fun start() {
        val handle1 = first.invokeWhen { value = merger(it, second.value) }
        val handle2 = second.invokeWhen { value = merger(first.value, it) }
        handle = handle1 + handle2
    }

    override fun stop() {
        handle.dispose()
    }

}