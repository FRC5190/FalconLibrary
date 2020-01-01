/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors.ctre

import com.ctre.phoenix.motorcontrol.MotorCommutation
import com.ctre.phoenix.motorcontrol.StatorCurrentLimitConfiguration
import com.ctre.phoenix.motorcontrol.SupplyCurrentLimitConfiguration
import com.ctre.phoenix.motorcontrol.can.TalonFX
import com.ctre.phoenix.sensors.SensorInitializationStrategy
import kotlin.properties.Delegates
import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitModel

/**
 * Wrapper around the TalonFX motor controller.
 *
 * @param talonFX The underlying TalonFX motor controller.
 * @param model The native unit model.
 */
@Suppress("Unused")
class FalconFX<K : SIKey>(
    @Suppress("MemberVisibilityCanBePrivate") val talonFX: TalonFX,
    model: NativeUnitModel<K>
) : FalconBaseTalon<K>(talonFX, model) {

    /**
     * Alternate constructor where users can supply ID and native unit model.
     *
     * @param id The ID of the motor controller.
     * @param model The native unit model.
     */
    constructor(id: Int, model: NativeUnitModel<K>) : this(TalonFX(id), model)

    /**
     * Configures the motor commutation type for the Falcon 500.
     */
    var motorCommutation by Delegates.observable(MotorCommutation.Trapeziodal) { _, _, newValue ->
        talonFX.configMotorCommutation(newValue, 0)
    }

    /**
     * Configures the sensor initialization strategy for the motor controller. This can be used
     * to set whether the sensor starts at zero or to the position reported by the absolute encoder.
     * The latter is useful for mechanisms such as swerve drives or turrets where initial absolute
     * positioning is needed, but relative mode is used after boot.
     */
    var sensorInitializerStrategy by Delegates.observable(SensorInitializationStrategy.BootToZero) { _, _, newValue ->
        talonFX.configIntegratedSensorInitializationStrategy(newValue, 0)
    }

    /**
     * Configures the supply-side current limit for the motor.
     *
     * @param config The supply-side current limit configuration.
     */
    fun configSupplyCurrentLimit(config: SupplyCurrentLimitConfiguration) {
        talonFX.configSupplyCurrentLimit(config, 0)
    }

    /**
     * Configures the current limit for the stator of the motor.
     *
     * @param config The stator current limit configuration.
     */
    fun configStatorCurrentLimit(config: StatorCurrentLimitConfiguration) {
        talonFX.configStatorCurrentLimit(config, 0)
    }
}

fun <K : SIKey> falconFX(
    talonFX: TalonFX,
    model: NativeUnitModel<K>,
    block: FalconFX<K>.() -> Unit
) = FalconFX(talonFX, model).also(block)

fun <K : SIKey> falconFX(
    id: Int,
    model: NativeUnitModel<K>,
    block: FalconFX<K>.() -> Unit
) = FalconFX(id, model).also(block)
