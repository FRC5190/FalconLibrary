package frc.team5190.lib.utils

import edu.wpi.first.wpilibj.AnalogInput
import kotlinx.coroutines.experimental.DisposableHandle
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.NonDisposableHandle
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.CopyOnWriteArrayList

// Basic Implementation

interface State<T> : Source<T> {
    override val value: T

    // Default implementations
    fun invokeOnChange(listener: StateListener<T>): DisposableHandle

    fun invokeWhen(state: T, ignoreCurrent: Boolean = false, listener: StateListener<T>) = invokeWhen(listOf(state), ignoreCurrent, listener)
    fun invokeWhen(state: List<T>, ignoreCurrent: Boolean = false, listener: StateListener<T>): DisposableHandle

    // Shortcut implementations
    fun invokeOnceOnChange(listener: StateListener<T>) = invokeOnChange {
        listener(this, it)
        dispose()
    }

    fun invokeOnceWhen(state: T, ignoreCurrent: Boolean = false, listener: StateListener<T>) = invokeOnceWhen(listOf(state), ignoreCurrent, listener)
    fun invokeOnceWhen(state: List<T>, ignoreCurrent: Boolean = false, listener: StateListener<T>) = invokeWhen(state, ignoreCurrent) {
        listener(this, it)
        dispose()
    }
}

abstract class StateImpl<T>(initValue: T) : State<T> {
    private val syncValue = Any()

    override val value: T
        get() = internalValue

    var internalValue = initValue
        set(value) {
            synchronized(syncValue) {
                if (field != value) changeListeners.forEach { it.second(it.first, value) }
                field = value
            }
        }

    private val changeListeners = CopyOnWriteArrayList<Pair<DisposableHandle, StateListener<T>>>()

    private fun removeListener(listener: StateListener<T>) = synchronized(syncValue) {
        changeListeners.removeIf { it.second == listener }
        if (changeListeners.isEmpty()) disposeWhenUnused()
    }

    private var hasInit = false
    protected open fun initWhenUsed() {}
    protected open fun disposeWhenUnused() {}

    override fun invokeOnChange(listener: StateListener<T>): DisposableHandle = synchronized(syncValue) {
        if (!hasInit) {
            hasInit = true
            initWhenUsed()
        }
        val handle = object : DisposableHandle {
            override fun dispose() {
                removeListener(listener)
            }
        }
        changeListeners.add(handle to listener)
        return handle
    }

    override fun invokeWhen(state: List<T>, ignoreCurrent: Boolean, listener: StateListener<T>): DisposableHandle = synchronized(syncValue) {
        val changeListener = invokeOnChange { if (state.contains(it)) listener(this, it) }
        if (state.contains(value)) listener(changeListener, value)
        return changeListener
    }
}

fun <F, T> processedState(state: State<F>, processing: (F) -> T) = processedState(listOf(state)) { values -> processing(values.first()) }

fun <F, T> processedState(states: List<State<out F>>, processing: (List<F>) -> T): State<T> =
        object : StateImpl<T>(processing(states.map { it.value })) {

            override val value: T
                get() = synchronized(handles) {
                    if (handles.isEmpty()) {
                        // If no listeners are registered just grab the new value and update it manually
                        val newValue = processing(states.map { it.value })
                        internalValue = newValue
                        newValue
                    } else super.value
                }

            private val handles = mutableListOf<DisposableHandle>()

            override fun initWhenUsed() = synchronized(handles) {
                states.forEach { state ->
                    handles += state.invokeOnChange { value ->
                        val newValues = states.map { stateVal -> if (stateVal == state) value else stateVal.value }
                        synchronized(handles) {
                            internalValue = processing(newValues)
                        }
                    }
                }
                internalValue = processing(states.map { it.value })
            }

            override fun disposeWhenUnused() = synchronized(handles) {
                handles.forEach { it.dispose() }
                handles.clear()
            }
        }

fun <T> constState(value: T) = object : State<T> {
    override val value = value
    override fun invokeOnChange(listener: StateListener<T>) = NonDisposableHandle
    override fun invokeWhen(state: List<T>, ignoreCurrent: Boolean, listener: StateListener<T>) = NonDisposableHandle
}

typealias StateListener<T> = DisposableHandle.(T) -> Unit

// Comparision State

fun <F> comparisionState(one: State<out F>, two: State<out F>, processing: (F, F) -> Boolean): BooleanState =
        processedState(listOf(one, two)) { values -> processing(values[0], values[1]) }

// Variable State

fun <T> variableState(initValue: T): VariableState<T> = VariableStateImpl(initValue)

private class VariableStateImpl<T>(initValue: T) : StateImpl<T>(initValue), VariableState<T> {
    override var value: T
        set(value) {
            internalValue = value
        }
        get() = super.value
}

interface VariableState<T> : State<T> {
    override var value: T
}

// Updatable State

fun <T> updatableState(frequency: Int = 50, block: () -> T): StateImpl<T> = UpdatableState(frequency, block)

private class UpdatableState<T>(private val frequency: Int = 50, private val block: () -> T) : StateImpl<T>(block()) {
    companion object {
        private val context = newSingleThreadContext("Updatable State")
    }

    private lateinit var job: Job

    override fun initWhenUsed() {
        job = launchFrequency(frequency, context) {
            internalValue = block()
        }
    }

    override fun disposeWhenUnused() {
        job.cancel()
    }
}

// Boolean State

typealias BooleanState = State<Boolean>
typealias BooleanListener = StateListener<Boolean>


fun BooleanState.invokeWhenTrue(ignoreCurrent: Boolean = false, listener: BooleanListener) = invokeWhen(true, ignoreCurrent, listener)
fun BooleanState.invokeWhenFalse(ignoreCurrent: Boolean = false, listener: BooleanListener) = invokeWhen(false, ignoreCurrent, listener)

operator fun BooleanState.not(): BooleanState = object : BooleanState {
    override val value: Boolean
        get() = !this@not.value

    override fun invokeOnChange(listener: StateListener<Boolean>) = this@not.invokeOnChange { listener(this, !it) }
    override fun invokeWhen(state: List<Boolean>, ignoreCurrent: Boolean, listener: StateListener<Boolean>) =
            this@not.invokeWhen(state, ignoreCurrent) { listener(this, !it) }
}

// Extensions

operator fun <T, V> Map<T, V>.get(key: State<T>): State<V?> = processedState(key) { this@get[it] }

// Sensor Extensions

val AnalogInput.voltageState
    get() = voltageState()

fun AnalogInput.voltageState(frequency: Int = 50) =
        updatableState(frequency) { this@voltageState.averageVoltage }