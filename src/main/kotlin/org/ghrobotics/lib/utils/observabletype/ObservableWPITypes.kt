package org.ghrobotics.lib.utils.observabletype

import edu.wpi.first.wpilibj.AnalogInput
import kotlinx.coroutines.experimental.DefaultDispatcher
import kotlin.coroutines.experimental.CoroutineContext

// Analog Input

fun AnalogInput.asObservableVoltage(
        frequency: Int = 50,
        context: CoroutineContext = DefaultDispatcher
): ObservableValue<Double> = UpdatableObservableValue(frequency, context) { this.averageVoltage }