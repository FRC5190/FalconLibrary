package org.ghrobotics.lib.utils.observabletype

import kotlinx.coroutines.experimental.DisposableHandle

typealias ObservableListener<T> = ObservableHandle.(T) -> Unit

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