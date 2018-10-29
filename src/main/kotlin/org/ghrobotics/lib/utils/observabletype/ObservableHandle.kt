package org.ghrobotics.lib.utils.observabletype

import kotlinx.coroutines.DisposableHandle

typealias ObservableListener<T> = ObservableHandle.(T) -> Unit

fun <T> ObservableListener<T>.thenDispose(): ObservableListener<T> = {
    try {
        this@thenDispose(it)
    } finally {
        dispose()
    }
}

interface ObservableHandle : DisposableHandle {
    operator fun plus(other: ObservableHandle): ObservableHandle {
        fun mainDispose() = dispose()
        return object : ObservableHandle {
            override fun dispose() {
                mainDispose()
                other.dispose()
            }
        }
    }
}

object NonObservableHandle : ObservableHandle {
    override fun dispose() {}
}