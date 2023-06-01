/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.PneumaticsModuleType
import edu.wpi.first.wpilibj.Solenoid
import kotlin.properties.Delegates

/**
 * Interface for both double- and single-solenoids.
 */
interface FalconSolenoid {
    enum class State {
        Forward,
        Reverse,
        Off,
    }

    var state: State
}

/**
 * Single-acting solenoid with only one PCM Solenoid channel.
 * This type of solenoid can only be on or off.
 */
class FalconSingleSolenoid(channel: Int, moduleType: PneumaticsModuleType, module: Int? = null) : FalconSolenoid {
    private val wpiSolenoid: Solenoid = if (module == null) Solenoid(moduleType, channel) else Solenoid(module, moduleType, channel)

    // Set the solenoid's position. Forward -> true, Reverse -> false
    // If the solenoid is set to 'Off', the state is not changed.
    override var state: FalconSolenoid.State by Delegates.observable(FalconSolenoid.State.Reverse) { _, _, newValue ->
        when (newValue) {
            FalconSolenoid.State.Forward -> wpiSolenoid.set(true)
            FalconSolenoid.State.Reverse -> wpiSolenoid.set(false)
            else -> Unit
        }
    }
}

/**
 * Double-acting solenoid with two PCM Solenoid channels.
 * This type of solenoid can be forward (Forward channel is active), reverse (Reverse channel is active),
 * or off (Neither of the two channels are active).
 */
class FalconDoubleSolenoid(forwardChannel: Int, reverseChannel: Int, moduleType: PneumaticsModuleType, module: Int? = null) :
    FalconSolenoid {

    private val wpiSolenoid: DoubleSolenoid =
        if (module == null) {
            DoubleSolenoid(moduleType, forwardChannel, reverseChannel)
        } else {
            DoubleSolenoid(module, moduleType, forwardChannel, reverseChannel)
        }

    // Set the solenoid to the desired position
    override var state: FalconSolenoid.State by Delegates.observable(FalconSolenoid.State.Off) { _, _, newValue ->
        when (newValue) {
            FalconSolenoid.State.Forward -> wpiSolenoid.set(DoubleSolenoid.Value.kForward)
            FalconSolenoid.State.Reverse -> wpiSolenoid.set(DoubleSolenoid.Value.kReverse)
            FalconSolenoid.State.Off -> wpiSolenoid.set(DoubleSolenoid.Value.kOff)
        }
    }
}
