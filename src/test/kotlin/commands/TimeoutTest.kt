package frc.team5190.lib.commands

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class TimeoutTest {

    @Test
    fun timeoutTest() = runBlocking{
        val run = InstantRunnableCommand {}
        run.start()
        run.await()

        val delay = DelayCommand(5, TimeUnit.SECONDS)
        val delayWithTimeout = DelayCommand(5, TimeUnit.SECONDS).withTimeout(2, TimeUnit.SECONDS)

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