package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.*
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second
import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableListener
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableVariable

class DelayCommand(private val delaySource: Source<Time>) : FalconCommand() {
    init {
        executeFrequency = 0
    }

    constructor(delay: Time) : this(Source(delay))

    override suspend fun initialize() {
        withTimeout(delaySource.value)
    }
}

class StatefulDelayImpl(
    override var delay: Time
) : ObservableValue<Boolean>, StatefulDelay {
    companion object {
        private val timeoutScope = CoroutineScope(newSingleThreadContext("Delay Condition"))
    }

    private val delayValue = ObservableVariable(false)

    override var value by delayValue
        private set

    override fun invokeOnSet(listener: ObservableListener<Boolean>) = delayValue.invokeOnSet(listener)

    private lateinit var job: Job
    private var startTime = 0.second

    fun start(startTime: Time) {
        value = false
        this.startTime = startTime
        job = timeoutScope.launch {
            delay((delay - (System.nanoTime().nanosecond - startTime)).millisecond.asLong)
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
    val delay: Time
}