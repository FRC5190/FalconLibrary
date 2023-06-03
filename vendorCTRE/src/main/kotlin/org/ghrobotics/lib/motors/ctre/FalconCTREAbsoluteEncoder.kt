/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.FeedbackDevice
import com.ctre.phoenix.motorcontrol.can.TalonSRX
import edu.wpi.first.util.sendable.SendableBuilder
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Radian
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnits
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100ms
import org.ghrobotics.lib.motors.AbstractFalconAbsoluteEncoder

class FalconCTREAbsoluteEncoder(
    private val canTalonSRX: TalonSRX,
    model: NativeUnitModel<Radian>,
    var offset: SIUnit<Radian> = 0.0.radians,
) : AbstractFalconAbsoluteEncoder<Radian>(model) {

    init {
        canTalonSRX.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Absolute)
    }

    constructor(id: Int, model: NativeUnitModel<Radian>, offset: SIUnit<Radian> = 0.0.radians) : this(
        TalonSRX(id),
        model,
        offset,
    )

    override val absolutePosition: SIUnit<Radian>
        get() = position + offset
    override val rawPosition: SIUnit<NativeUnit>
        get() = canTalonSRX.selectedSensorPosition.nativeUnits
    override val rawVelocity: SIUnit<NativeUnitVelocity>
        get() = canTalonSRX.selectedSensorVelocity.nativeUnitsPer100ms

    override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {
        offset = model.fromNativeUnitPosition(newPosition)
    }

    override fun resetPosition(newPosition: SIUnit<Radian>) {
        offset = newPosition
    }

    var inverted: Boolean
        get() = canTalonSRX.inverted
        set(v) {
            canTalonSRX.inverted = v
        }

    override fun initSendable(builder: SendableBuilder?) {
        builder?.addDoubleProperty("Encoder Value", {
            absolutePosition.value
        }, {})
    }
}
