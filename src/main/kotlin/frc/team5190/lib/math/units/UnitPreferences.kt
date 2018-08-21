/*
 * FRC Team 5190
 * Green Hope Falcons
 */

package frc.team5190.lib.math.units

data class UnitPreferences(var sensorUnitsPerRotation: Int = 1440,
                           var radius: Double = 3.0)



fun preferences(create: UnitPreferences.() -> Unit): UnitPreferences {
    val settings = UnitPreferences()
    create.invoke(settings)
    return settings
}

