/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.runBlocking
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.millisecond
import org.ghrobotics.lib.mathematics.units.nanosecond
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test
import kotlin.system.measureTimeMillis

class CommandGroupTest {

    private fun delayHelper(delay: Time, name: String) = object : Command() {
        init {
            withTimeout(delay)
        }

        override suspend fun initialize() = println("$name > START")
        override suspend fun dispose() {
            println("$name > ${(System.nanoTime().nanosecond - startTime).millisecond.asDouble}")
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
                +parallel {
                    +delayHelper(1.second, "S1 -> P1 -> 1")
                    +sequential {
                        +delayHelper(1.second, "S1 -> P1 -> 2")
                        +sequential {
                            +delayHelper(1.second, "S1 -> P1 -> S1 -> 3")
                            +delayHelper(1.second, "S1 -> P1 -> S1 -> 4")
                        }
                        +delayHelper(1.second, "S1 -> P1 -> 5")
                    }
                }
            }
            command.commandState.invokeOnChange {
                println("COMMAND $it")
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

    @Test
    fun testGroupTimeout() = runBlocking {
        var delayEndTime = 0L

        val command = sequential {
            +object : Command() {
                init {
                    withTimeout(5.second)
                }

                override suspend fun dispose() {
                    delayEndTime = System.nanoTime()
                }
            }
        }.withTimeout(500.millisecond)

        var commandStartTime = 0L

        command.commandState.invokeOnceWhen(CommandState.BAKING) {
            commandStartTime = System.nanoTime()
        }

        command.start()
        command.await()

        val deltaTime = (delayEndTime - commandStartTime).toDouble() / 1.0e6

        assert(500 - Math.abs(deltaTime) < 10)
    }
}