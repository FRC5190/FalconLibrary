package org.ghrobotics.lib.utils.observabletype

import kotlin.reflect.KProperty

interface ObservableVariable<T> : ObservableValue<T> {
    override var value: T

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }

    companion object {
        operator fun <T> invoke(value: T): ObservableVariable<T> = ObservableVariableImpl(value)
        operator fun <T> invoke(): ObservableVariable<T?> = ObservableVariableImpl(null)
    }
}

private class ObservableVariableImpl<T>(value: T) : ObservableVariable<T>, SubscribableObservableValueImpl<T>() {
    override var value = value
        set(value) {
            informListeners(value)
            field = value
        }
}