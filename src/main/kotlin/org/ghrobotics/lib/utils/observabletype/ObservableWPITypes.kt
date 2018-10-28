package org.ghrobotics.lib.utils.observabletype

import edu.wpi.first.wpilibj.AnalogInput
import kotlinx.coroutines.experimental.CoroutineScope

// Analog Input

fun AnalogInput.asObservableVoltage(
        scope: CoroutineScope,
        frequency: Int = 50
): ObservableValue<Double> = scope.updatableValue(frequency) { this.averageVoltage }