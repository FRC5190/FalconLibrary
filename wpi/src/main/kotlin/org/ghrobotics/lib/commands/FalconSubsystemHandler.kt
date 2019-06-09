package org.ghrobotics.lib.commands

internal object FalconSubsystemHandler {
    private val registeredSubsystems = arrayListOf<FalconSubsystem>()

    fun add(subsystem: FalconSubsystem) {
        registeredSubsystems.add(subsystem)
    }

    fun lateInit() = registeredSubsystems.forEach { it.lateInit() }
    fun autoReset() = registeredSubsystems.forEach { it.autoReset() }
    fun teleopReset() = registeredSubsystems.forEach { it.teleopReset() }
    fun setNeutral() = registeredSubsystems.forEach { it.setNeutral() }
}