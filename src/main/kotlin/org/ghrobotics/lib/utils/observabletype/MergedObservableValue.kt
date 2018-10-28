package org.ghrobotics.lib.utils.observabletype

@JvmName("plusDouble")
operator fun ObservableValue<Double>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toDouble() }

@JvmName("plusFloat")
operator fun ObservableValue<Float>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toFloat() }

@JvmName("plusLong")
operator fun ObservableValue<Long>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toLong() }

@JvmName("plusInt")
operator fun ObservableValue<Int>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toInt() }

@JvmName("plusShort")
operator fun ObservableValue<Short>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toShort() }

@JvmName("plusByte")
operator fun ObservableValue<Byte>.plus(other: ObservableValue<Number>) =
        mergeWith(other) { one, two -> one + two.toByte() }

@JvmName("plusString")
operator fun ObservableValue<String>.plus(other: ObservableValue<String>) = mergeWithString(other)

fun ObservableValue<String>.mergeWithString(
        other: ObservableValue<String>,
        separator: String = " "
) = mergeWith(other) { one, two -> "$one$separator$two" }

@JvmName("plusAny")
operator fun <F1, F2> ObservableValue<F1>.plus(other: ObservableValue<F2>) =
        mergeWith(other) { one, two -> one to two }

fun <F1, F2, T> ObservableValue<F1>.mergeWith(
        with: ObservableValue<F2>,
        merger: (F1, F2) -> T
): ObservableValue<T> = MergedObservableValueImpl(this, with, merger)

private class MergedObservableValueImpl<F1, F2, T>(
        val first: ObservableValue<F1>,
        val second: ObservableValue<F2>,
        val merger: (F1, F2) -> T
) : ObservableValue<T>, SubscribableObservableValueImpl<T>() {

    override var value: T = merger(first.value, second.value)
        private set(value) {
            informListeners(value)
            field = value
        }
        get() {
            val newValue = merger(first.value, second.value)
            value = newValue
            return newValue
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

    override fun toString() = "MERGED($first, $second)[$value]"

}