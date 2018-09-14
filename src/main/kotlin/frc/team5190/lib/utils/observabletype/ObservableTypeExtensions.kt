package frc.team5190.lib.utils.observabletype

fun ObservableValue<Boolean>.invokeOnTrue(listener: ObservableListener<Boolean>) = invokeOnSet(true, listener = listener)
fun ObservableValue<Boolean>.invokeOnFalse(listener: ObservableListener<Boolean>) = invokeOnSet(false, listener = listener)

fun ObservableValue<Boolean>.invokeOnceOnTrue(listener: ObservableListener<Boolean>) = invokeOnceOnSet(true, listener = listener)
fun ObservableValue<Boolean>.invokeOnceOnFalse(listener: ObservableListener<Boolean>) = invokeOnceOnSet(false, listener = listener)

fun ObservableValue<Boolean>.invokeWhenTrue(listener: ObservableListener<Boolean>) = invokeWhen(true, listener = listener)
fun ObservableValue<Boolean>.invokeWhenFalse(listener: ObservableListener<Boolean>) = invokeWhen(false, listener = listener)

fun ObservableValue<Boolean>.invokeOnceWhenTrue(listener: ObservableListener<Boolean>) = invokeOnceWhen(true, listener = listener)
fun ObservableValue<Boolean>.invokeOnceWhenFalse(listener: ObservableListener<Boolean>) = invokeOnceWhen(false, listener = listener)

infix fun ObservableValue<Boolean>.and(other: Boolean) = and(ObservableValue(other))
infix fun ObservableValue<Boolean>.or(other: Boolean) = or(ObservableValue(other))
infix fun ObservableValue<Boolean>.xor(other: Boolean) = xor(ObservableValue(other))

infix fun ObservableValue<Boolean>.and(other: ObservableValue<Boolean>) = MergedObservableValue(this, other) { one, two -> one && two }
infix fun ObservableValue<Boolean>.or(other: ObservableValue<Boolean>) = MergedObservableValue(this, other) { one, two -> one || two }
infix fun ObservableValue<Boolean>.xor(other: ObservableValue<Boolean>) = MergedObservableValue(this, other) { one, two -> one xor two }

operator fun ObservableValue<Boolean>.not() = withProcessing { !it }