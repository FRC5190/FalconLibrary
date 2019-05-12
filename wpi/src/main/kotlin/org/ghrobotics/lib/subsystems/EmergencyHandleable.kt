package org.ghrobotics.lib.subsystems

interface EmergencyHandleable {
    fun activateEmergency()
    fun recoverFromEmergency()
}