/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.BaseTalon
import kotlin.properties.Delegates
import org.ghrobotics.lib.mathematics.units.Ampere
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.amps
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

/**
 * Abstract class for wrapper around Base Talon motor controllers i.e.
 * Talon SRX and Talon FX.
 */
abstract class FalconBaseTalon<K : SIKey>(
    private val baseTalon: BaseTalon,
    model: NativeUnitModel<K>
) : FalconCTRE<K>(baseTalon, model) {
    /**
     * Configures the feedback sensor for PID and other closed loop control.
     */
    var feedbackSensor by Delegates.observable(FeedbackDevice.IntegratedSensor) { _, _, newValue ->
        baseTalon.configSelectedFeedbackSensor(newValue, 0, 0)
    }

    /**
     * Returns the drawn current from the motor.
     * @return The drawn current from the motor.
     */
    override val drawnCurrent: SIUnit<Ampere>
        get() = baseTalon.outputCurrent.amps
}
