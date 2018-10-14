package org.ghrobotics.lib.utils.observabletype

import org.ghrobotics.lib.utils.Source
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

interface ObservableValue<T> : ReadOnlyProperty<Any?, T> {
    val value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value

    // Basic Invoke On

    fun invokeOnSet(listener: ObservableListener<T>): ObservableHandle

    // Helper Implementations

    fun invokeOnSet(vararg values: T, listener: ObservableListener<T>) = invokeOnSet {
        if (values.contains(it)) listener(this, it)
    }

    fun invokeOnceOnSet(listener: ObservableListener<T>) = invokeOnSet(listener.thenDispose())
    fun invokeOnceOnSet(vararg values: T, listener: ObservableListener<T>) =
        invokeOnSet(*values, listener = listener.thenDispose())

    // Invoke On Change

    fun invokeOnChange(listener: ObservableListener<T>): ObservableHandle {
        var currentValue = value
        return invokeOnSet {
            if (currentValue != it) listener(this, it)
            currentValue = it
        }
    }

    fun invokeOnceOnChange(listener: ObservableListener<T>) = invokeOnChange(listener.thenDispose())

    fun invokeOnChangeTo(vararg values: T, listener: ObservableListener<T>) =
        invokeOnChangeTo(values.asList(), listener)

    fun invokeOnChangeTo(values: List<T>, listener: ObservableListener<T>): ObservableHandle {
        var firstRun = true
        var currentValue = value
        return invokeOnSet {
            if (values.contains(it) && (currentValue != it || firstRun)) listener(this, it)
            currentValue = it
            firstRun = false
        }
    }

    // Invoke When

    fun invokeWhen(vararg values: T, listener: ObservableListener<T>) = invokeWhen(values.asList(), listener)

    fun invokeWhen(values: Collection<T>, listener: ObservableListener<T>): ObservableHandle {
        var firstRun = true
        var currentValue = value
        return invokeWhen {
            if (values.contains(it) && (currentValue != it || firstRun)) listener(this, it)
            currentValue = it
            firstRun = false
        }
    }

    fun invokeWhen(listener: ObservableListener<T>): ObservableHandle {
        val syncValue = Any()
        var currentValue = value
        synchronized(syncValue) {
            val handle = invokeOnSet {
                synchronized(syncValue) {
                    if (currentValue != it) listener(this, it)
                    currentValue = it
                }
            }
            currentValue = value
            listener(handle, currentValue)
            return handle
        }
    }

    fun invokeOnceWhen(listener: ObservableListener<T>) = invokeWhen(listener = listener.thenDispose())
    fun invokeOnceWhen(vararg values: T, listener: ObservableListener<T>) =
        invokeWhen(*values, listener = listener.thenDispose())

    fun asSource() = Source { value }

    // Comparison

    fun <F : Comparable<T>> greaterThan(other: F) = greaterThan(ObservableValue(other))
    fun <F : Comparable<T>> greaterThanOrEquals(other: F) = greaterThanOrEquals(ObservableValue(other))
    fun <F : Comparable<T>> lessThan(other: F) = lessThan(ObservableValue(other))
    fun <F : Comparable<T>> lessThanOrEquals(other: F) = lessThanOrEquals(ObservableValue(other))
    fun <F : Comparable<T>> compareTo(other: F) = compareTo(ObservableValue(other))

    fun <F : Comparable<T>> greaterThan(other: ObservableValue<F>) = compareToInternal(other) { it > 0 }
    fun <F : Comparable<T>> greaterThanOrEquals(other: ObservableValue<F>) = compareToInternal(other) { it >= 0 }
    fun <F : Comparable<T>> lessThan(other: ObservableValue<F>) = compareToInternal(other) { it < 0 }
    fun <F : Comparable<T>> lessThanOrEquals(other: ObservableValue<F>) = compareToInternal(other) { it <= 0 }
    fun <F : Comparable<T>> compareTo(other: ObservableValue<F>) = compareToInternal(other) { it }

    private inline fun <F : Comparable<T>, R> compareToInternal(
        other: ObservableValue<F>,
        crossinline block: (Int) -> R
    ): ObservableValue<R> = this.mergeWith(other) { one, two -> block(-two.compareTo(one)) }

    companion object {
        operator fun <T> invoke(value: T): ObservableValue<T> = ObservableValueImpl(value)
        operator fun <T> invoke(): ObservableValue<T?> = ObservableValueImpl(null)
    }
}

private class ObservableValueImpl<T>(override val value: T) : ObservableValue<T> {
    override fun invokeOnSet(listener: ObservableListener<T>) = NonObservableHandle

    override fun toString() = "VAL[$value]"
}

