package frc.team5190.lib.utils.statefulvalue

import edu.wpi.first.wpilibj.AnalogInput

// Analog Input

val AnalogInput.voltageState
    get() = voltageState()

fun AnalogInput.voltageState(frequency: Int = 50): StatefulValue<Double> = StatefulValue(frequency) { this@voltageState.averageVoltage }