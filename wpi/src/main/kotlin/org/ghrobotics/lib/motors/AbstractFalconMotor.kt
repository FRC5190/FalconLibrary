/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors

import edu.wpi.first.hal.SimDevice
import edu.wpi.first.hal.SimDouble
import org.ghrobotics.lib.mathematics.units.SIKey

abstract class AbstractFalconMotor<K : SIKey>(simName: String) : FalconMotor<K> {

    private val simDevice: SimDevice? = SimDevice.create(simName)

    val simVoltageOutput: SimDouble? = simDevice?.createDouble("Voltage output", true, 0.0);

    override var useMotionProfileForPosition: Boolean = false

    override fun follow(motor: FalconMotor<*>): Boolean {
        TODO("Cross brand motor controller following not yet implemented!")
    }
}
