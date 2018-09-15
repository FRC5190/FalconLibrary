package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.observabletype.ObservableListener
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableVariable
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.TimeUnit

class DelayCommand(delay: Long, unit: TimeUnit = TimeUnit.SECONDS) : Command() {
    init {
        executeFrequency = 0
        withTimeout(delay, unit)
    }
}

class StatefulDelayImpl(
        override var delay: Long,
        override var unit: TimeUnit = TimeUnit.SECONDS
) : ObservableValue<Boolean>, StatefulDelay {
    companion object {
        private val timeoutContext = newSingleThreadContext("Delay Condition")
    }

    private val delayValue = ObservableVariable(false)

    override var value by delayValue
        private set

    override fun invokeOnSet(listener: ObservableListener<Boolean>) = delayValue.invokeOnSet(listener)

    private lateinit var job: Job
    private var startTime = 0L

    fun start(startTime: Long) {
        value = false
        this.startTime = startTime
        job = launch(timeoutContext) {
            delay(unit.toNanos(delay) - (System.nanoTime() - startTime), TimeUnit.NANOSECONDS)
            value = true
        }
    }

    fun stop() {
        job.cancel()
        value = false
    }
}

interface StatefulDelay : ObservableValue<Boolean> {
    val delay: Long
    val unit: TimeUnit
}