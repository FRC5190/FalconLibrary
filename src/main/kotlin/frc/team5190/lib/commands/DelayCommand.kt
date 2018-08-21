package frc.team5190.lib.commands

import frc.team5190.lib.utils.StateImpl
import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext
import java.util.concurrent.TimeUnit

class DelayCommand(delay: Long, unit: TimeUnit = TimeUnit.SECONDS) : Command() {
    init {
        updateFrequency = 0
        withTimeout(delay, unit)
    }
}

class DelayCondition(var delay: Long, var unit: TimeUnit) : StateImpl<Boolean>(false) {

    companion object {
        private val timeoutContext = newSingleThreadContext("Delay Condition")
    }

    private lateinit var job: Job
    private var startTime = 0L

    fun start(startTime: Long) {
        internalValue = false
        this.startTime = startTime
        job = launch(timeoutContext) {
            delay(unit.toNanos(delay) - (System.nanoTime() - startTime), TimeUnit.NANOSECONDS)
            internalValue = true
        }
    }

    fun stop() {
        job.cancel()
        internalValue = false
    }
}