package org.ghrobotics.lib.utils.observabletype

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun <T> ObservableVariable(value: T): ObservableVariable<T> = ObservableVariableImpl(value)
fun <T> ObservableVariable(): ObservableVariable<T?> = ObservableVariableImpl(null)

interface ObservableVariable<T> : ObservableValue<T>, ReadWriteProperty<Any?, T> {
    override var value: T

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = value
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        this.value = value
    }
}

private class ObservableVariableImpl<T>(value: T) : ObservableVariable<T>, SubscribableObservableValueImpl<T>() {
    override var value = value
        set(value) {
            informListeners(value)
            field = value
        }

    override fun toString() = "VAR[$value]"
}