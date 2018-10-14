package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.runBlocking
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import kotlin.system.measureTimeMillis

class TimeoutTest {

    @Test
    fun timeoutTest() = runBlocking {
        val run = InstantRunnableCommand {}
        run.start()
        run.await()

        val delay = DelayCommand(5.second)
        val delayWithTimeout = DelayCommand(5.second).withTimeout(2.second)

        val time1 = measureTimeMillis {
            delay.start()
            delay.await()
        }
        println("Without Timeout: $time1 ms")
        val time2 = measureTimeMillis {
            delayWithTimeout.start()
            delayWithTimeout.await()
        }
        println("With Timeout $time2 ms")
    }

}