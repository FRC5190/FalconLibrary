package org.ghrobotics.lib.commands

import kotlinx.coroutines.experimental.runBlocking
import org.junit.Test
import java.util.concurrent.TimeUnit

class ParallelSameTest {

    private object FakeSubsystem : Subsystem() {
        override fun autoReset() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun teleopReset() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

        override fun zeroOutputs() {
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }

    }

    private fun testCommand(id: Int) = object : Command(FakeSubsystem) {
        override suspend fun initialize() {
            println("Start #$id")
        }

        override suspend fun dispose() {
            println("Stop #$id")
        }
    }.withTimeout(5, TimeUnit.SECONDS)

    @Test
    fun testSameSubsystem() = runBlocking {
        SubsystemHandler.addSubsystem(FakeSubsystem)

        var realStartTime = 0L
        val group = parallel {
            +InstantRunnableCommand { realStartTime = System.currentTimeMillis() }
            +sequential {
                +DelayCommand(1, TimeUnit.SECONDS)
                +testCommand(1)
                +testCommand(3)
            }
            +testCommand(2)
        }

        group.start()
        group.await()
        val endTime = System.currentTimeMillis()
        println("Took ${(endTime - realStartTime) / 1000.0} seconds")
    }

}