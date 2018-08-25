package frc.team5190.lib.commands

import frc.team5190.lib.utils.statefulvalue.StatefulValue
import frc.team5190.lib.utils.statefulvalue.StatefulValueImpl
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
) : StatefulValueImpl<Boolean>(false), StatefulDelay {

    companion object {
        private val timeoutContext = newSingleThreadContext("Delay Condition")
    }

    private lateinit var job: Job
    private var startTime = 0L

    fun start(startTime: Long) {
        changeValue(false)
        this.startTime = startTime
        job = launch(timeoutContext) {
            delay(unit.toNanos(delay) - (System.nanoTime() - startTime), TimeUnit.NANOSECONDS)
            changeValue(true)
        }
    }

    fun stop() {
        job.cancel()
        changeValue(false)
    }
}

interface StatefulDelay : StatefulValue<Boolean> {
    val delay: Long
    val unit: TimeUnit
}