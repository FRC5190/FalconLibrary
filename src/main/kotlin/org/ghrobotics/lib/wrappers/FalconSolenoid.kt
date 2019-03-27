package org.ghrobotics.lib.wrappers

import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Solenoid
import kotlin.properties.Delegates

/**
 * Interface for both double- and single-solenoids.
 */
interface FalconSolenoid {
    enum class State {
        Forward,
        Reverse,
        Off
    }

    var state: State
}

/**
 * Single-acting solenoid with only one PCM Solenoid channel.
 * This type of solenoid can only be on or off.
 */
class FalconSingleSolenoid(channel: Int, module: Int? = null) : FalconSolenoid {
    private val wpiSolenoid: Solenoid = if (module == null) Solenoid(channel) else Solenoid(module, channel)

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
class FalconDoubleSolenoid(forwardChannel: Int, reverseChannel: Int, module: Int? = null) : FalconSolenoid {

    private val wpiSolenoid: DoubleSolenoid =
            if (module == null) DoubleSolenoid(forwardChannel, reverseChannel)
            else DoubleSolenoid(module, forwardChannel, reverseChannel)

    // Set the solenoid to the desired position
    override var state: FalconSolenoid.State by Delegates.observable(FalconSolenoid.State.Off) { _, _, newValue ->
        when (newValue) {
            FalconSolenoid.State.Forward -> wpiSolenoid.set(DoubleSolenoid.Value.kForward)
            FalconSolenoid.State.Reverse -> wpiSolenoid.set(DoubleSolenoid.Value.kReverse)
            FalconSolenoid.State.Off -> wpiSolenoid.set(DoubleSolenoid.Value.kOff)
        }
    }
}
