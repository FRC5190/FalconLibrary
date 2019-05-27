package org.ghrobotics.lib.utils

import kotlinx.coroutines.*
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext



inline fun disposableHandle(crossinline block: () -> Unit) = object : DisposableHandle {
    override fun dispose() {
        block()
    }
}

fun disposableHandle(vararg handles: DisposableHandle) = disposableHandle(handles.asList())

fun disposableHandle(handles: Collection<DisposableHandle>) = disposableHandle {
    handles.forEach { it.dispose() }
}