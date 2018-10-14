package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.*
import org.ghrobotics.lib.utils.observabletype.ObservableListener
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableVariable
import java.util.concurrent.TimeUnit

class DelayCommand(delay: Long, unit: TimeUnit = TimeUnit.SECONDS) : Command() {

    constructor(delaySeconds: Double) : this((delaySeconds * 1000).toLong(), TimeUnit.MILLISECONDS)

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
        private val timeoutScope = CoroutineScope(newSingleThreadContext("Delay Condition"))
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
        job = timeoutScope.launch {
            delay(unit.toNanos(delay) - (System.nanoTime() - startTime), TimeUnit.NANOSECONDS)
            value = true
        }
    }

    suspend fun stop() {
        job.cancelAndJoin()
        value = false
    }

    override fun toString() = "DELAY($delayValue)"
}

interface StatefulDelay : ObservableValue<Boolean> {
    val delay: Long
    val unit: TimeUnit
}