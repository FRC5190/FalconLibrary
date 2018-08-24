/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.commands

import frc.team5190.lib.extensions.sequential
import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit
import kotlin.system.measureTimeMillis

class CommandGroupTest {

    private fun delayHelper(delay: Long, name: String) = object : Command() {
        init {
            withTimeout(delay, TimeUnit.SECONDS)
        }
        override suspend fun initialize() = println("$name > START")
        override suspend fun dispose() {
            println("$name > ${TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime)}")
        }
    }

    @Test
    fun testCommandGroup() {
        runBlocking {
            val start = object : InstantCommand() {
            }
            start.start()
            start.await()

            val command = sequential {
                parallel {
                    +delayHelper(1, "S1 -> P1 -> 1")
                    sequential {
                        +delayHelper(1, "S1 -> P1 -> 2")
                        sequential {
                            +delayHelper(1, "S1 -> P1 -> S1 -> 3")
                            +delayHelper(1, "S1 -> P1 -> S1 -> 4")
                        }
                        +delayHelper(1, "S1 -> P1 -> 5")
                    }
                }
            }
            val time1 = measureTimeMillis {
                command.start().await()
                command.await()
            }
            println("Took $time1 ms")
            val time2 = measureTimeMillis {
                command.start().await()
                command.await()
            }
            println("Took $time2 ms")
            assert(true)
        }


    }
}