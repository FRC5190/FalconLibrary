package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Subsystem
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicLong

internal object SubsystemHandler {

    private val subsystems = CopyOnWriteArrayList<FalconSubsystem>()

    private var alreadyStarted = false

    fun addSubsystem(subsystem: FalconSubsystem) {
        if (alreadyStarted) throw IllegalStateException("You cannot add a subsystem after the initialize stage")
        subsystems.add(subsystem)
        println("[FalconSubsystem Handler] Added ${subsystem.javaClass.simpleName}")
    }

    fun lateInit() = subsystems.forEach { it.lateInit() }

    fun autoReset() = subsystems.forEach { it.autoReset() }

    fun teleopReset() = subsystems.forEach { it.teleopReset() }

    // https://www.chiefdelphi.com/forums/showthread.php?t=166814
    fun zeroOutputs() = subsystems.forEach { it.zeroOutputs() }
}

/**
 *  Kotlin Wrapper for [WPI's Subsystem][Subsystem]
 *  @param name the name of the subsystem
 */
abstract class FalconSubsystem(name: String? = null) {
    companion object {
        private val subsystemId = AtomicLong()
    }

    val name = name ?: "FalconSubsystem ${subsystemId.incrementAndGet()}"
    private val _wpiSubsystem = FalconWpiSubsystem()
    /**
     * Wrapped WPI subsystem
     */
    val wpiSubsystem: Subsystem = _wpiSubsystem

    private inner class FalconWpiSubsystem : Subsystem(name) {
        override fun initDefaultCommand() {
            defaultCommand = this@FalconSubsystem.defaultCommand.wrappedValue
        }

        override fun periodic() {
            this@FalconSubsystem.periodic()
        }
    }

    /**
     * The default command, this is run when nothing else is currently requiring the subsystem
     */
    @Suppress("LeakingThis")
    var defaultCommand: FalconCommand = EmptyCommand(this)
        protected set(value) {
            _wpiSubsystem.defaultCommand = value.wrappedValue
            field = value
        }

    /**
     * Called after all subsystems can be initialized
     */
    open fun lateInit() {}

    /**
     * Called when autonomous mode starts
     */
    open fun autoReset() {}

    /**
     * Called when Tele-Operated mode starts
     */
    open fun teleopReset() {}

    /**
     * Called when no mode is enabled
     */
    open fun zeroOutputs() {}

    protected open fun periodic() {}
}