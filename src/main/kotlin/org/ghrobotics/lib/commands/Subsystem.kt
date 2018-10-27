package org.ghrobotics.lib.commands

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

    suspend fun autoReset() = subsystemMutex.withLock {
        subsystems.forEach { it.autoReset() }
    }

    suspend fun teleopReset() = subsystemMutex.withLock {
        subsystems.forEach { it.teleopReset() }
    }

    // https://www.chiefdelphi.com/forums/showthread.php?t=166814
    suspend fun zeroOutputs() = subsystemMutex.withLock {
        subsystems.forEach { it.zeroOutputs() }
    }
}

abstract class Subsystem(@Suppress("unused") val name: String) {
    companion object {
        private val subsystemId = AtomicLong()
    }

    constructor() : this("Subsystem ${subsystemId.incrementAndGet()}")

    var defaultCommand: FalconCommand? = null
        protected set

    open fun autoReset() {}
    open fun teleopReset() {}
    open fun zeroOutputs() {}
}