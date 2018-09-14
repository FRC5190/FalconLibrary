package frc.team5190.lib.utils.statefulvalue

import kotlinx.coroutines.experimental.newSingleThreadContext

private val referenceContext = newSingleThreadContext("Reference Thread")

class StatefulVariableReference<T>(reference: StatefulValue<T>) : StatefulValueReference<T>(reference) {
    private val updateLock = Any()
    override var reference = reference
        set(value) = synchronized(updateLock) {
            if (field == value) return@synchronized
            listenerHandle.dispose()
            field = value
            listenerHandle = listen(value)
        }
}

open class StatefulValueReference<T>(reference: StatefulValue<T>) : StatefulValueImpl<T>(reference.value) {
    open val reference = reference
    protected var listenerHandle = listen(reference)

    protected fun listen(reference: StatefulValue<T>) = reference.invokeOnChange(referenceContext, true) { changeValue(it) }
}