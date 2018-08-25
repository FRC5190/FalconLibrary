package frc.team5190.lib.commands

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import kotlin.system.measureNanoTime

class InstantCommandTest {

    @Test
    fun test() = runBlocking {
        val start = object : InstantCommand() {
        }
        start.start()
        start.await()

        val instantCommand = object : InstantCommand() {
            override suspend fun dispose() {
                val endTime = System.nanoTime()
                println("Init -> Dispose ${(endTime - startTime) / 1.0e+6}")
            }
        }
        instantCommand.start()
        instantCommand.await() // wait

        val instantCommand2 = object : InstantCommand() {
        }
        val time = measureNanoTime {
            instantCommand2.start()
            instantCommand2.await()
        }
        println("Start -> Await ${time / 1.0e+6}")

        val instantCommand3 = object : InstantCommand() {
        }
        instantCommand3.start()
        val time2 = measureNanoTime {
            instantCommand3.await()
        }
        println("Await ${time2 / 1.0e+6}")

        var start4 = 0L
        val instantCommand4 = object : InstantCommand() {
            override suspend fun initialize() {
                start4 = System.nanoTime()
            }
        }
        instantCommand4.start()
        instantCommand4.await()
        val end4 = System.nanoTime()
        println("Init -> Await ${(end4 - start4) / 1.0e+6}")

        val start5 = System.nanoTime()
        var end5 = 0L
        val instantCommand5 = object : InstantCommand() {
            override suspend fun initialize() {
                end5 = System.nanoTime()
            }
        }
        instantCommand5.start()
        instantCommand5.await()
        println("Start -> Init ${(end5 - start5) / 1.0e+6}")
    }

}