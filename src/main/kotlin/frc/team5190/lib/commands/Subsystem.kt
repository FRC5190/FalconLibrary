package frc.team5190.lib.commands

import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

object SubsystemHandler {

    private val subsystemMutex = Mutex()
    private val subsystems = CopyOnWriteArrayList<Subsystem>()

    private var alreadyStarted = false

    fun isRegistered(subsystem: Subsystem) = subsystems.contains(subsystem)

    suspend fun addSubsystem(subsystem: Subsystem) = subsystemMutex.withLock {
        if (alreadyStarted) throw IllegalStateException("You cannot add a subsystem after the initialize stage")
        subsystems.add(subsystem)
        println("[Subsystem Handler] Added ${subsystem.javaClass.simpleName}")
    }

    suspend fun startDefaultCommands() = subsystemMutex.withLock {
        if (alreadyStarted) throw IllegalStateException("Attempted to start default commands twice")
        alreadyStarted = true
        // Start default commands
        subsystems.forEach { it.defaultCommand?.start() }
    }

}

abstract class Subsystem(@Suppress("unused") val name: String) {
    companion object {
        private val subsystemId = AtomicLong()
    }

    constructor() : this("Subsystem ${subsystemId.incrementAndGet()}")

    var defaultCommand: Command? = null
        protected set
}