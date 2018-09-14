package frc.team5190.lib.utils.statefulvalue

import kotlin.coroutines.experimental.CoroutineContext

// Booleans

typealias StatefulBoolean = StatefulValue<Boolean>
typealias StatefulBooleanListener = StatefulListener<Boolean>

fun StatefulBoolean.invokeOnTrue(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnChangeTo(true, context, listener)
fun StatefulBoolean.invokeOnFalse(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnChangeTo(false, context, listener)

fun StatefulBoolean.invokeOnceOnTrue(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnceOnChangeTo(true, context, listener)
fun StatefulBoolean.invokeOnceOnFalse(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnceOnChangeTo(false, context, listener)

fun StatefulBoolean.invokeWhenTrue(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeWhen(true, context, listener)
fun StatefulBoolean.invokeWhenFalse(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeWhen(false, context, listener)

fun StatefulBoolean.invokeOnceWhenTrue(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnceWhen(true, context, listener)
fun StatefulBoolean.invokeOnceWhenFalse(context: CoroutineContext = STATEFUL_CONTEXT, listener: StatefulBooleanListener) = invokeOnceWhen(false, context, listener)

infix fun StatefulBoolean.and(other: Boolean): StatefulBoolean = and(StatefulValue(other))
infix fun StatefulBoolean.or(other: Boolean): StatefulBoolean = or(StatefulValue(other))
infix fun StatefulBoolean.xor(other: Boolean): StatefulBoolean = xor(StatefulValue(other))

infix fun StatefulBoolean.and(other: StatefulBoolean): StatefulBoolean = StatefulValue(this, other) { one, two -> one && two }
infix fun StatefulBoolean.or(other: StatefulBoolean): StatefulBoolean = StatefulValue(this, other) { one, two -> one || two }
infix fun StatefulBoolean.xor(other: StatefulBoolean): StatefulBoolean = StatefulValue(this, other) { one, two -> one xor two }

operator fun StatefulBoolean.not(): StatefulBoolean = withProcessing { !it }