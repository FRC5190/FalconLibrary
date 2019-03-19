package org.ghrobotics.lib.wrappers

import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Solenoid
import kotlin.properties.Delegates

/**
 * Interface for both double- and single-solenoids.
 */
interface TSolenoid {
    enum class State {
        Forward,
        Reverse,
        Off
    }

    var state : State
}

/**
 * Single-acting solenoid with only one PCM Solenoid channel.
 * This type of solenoid can only be on or off.
 */
class TSingleSolenoid(channel: Int, module: Int? = null) : TSolenoid {
    private val wpiSolenoid: Solenoid = if(module == null) Solenoid(channel) else Solenoid(module, channel)

    // Set the solenoid's position. Forward -> true, Reverse -> false
    // If the solenoid is set to 'Off', the state is not changed.
    override var state: TSolenoid.State by Delegates.observable(TSolenoid.State.Reverse) {_, _, newValue ->
        when (newValue) {
            TSolenoid.State.Forward -> wpiSolenoid.set(true)
            TSolenoid.State.Reverse -> wpiSolenoid.set(false)
            else -> Unit
        }
    }
}

/**
 * Double-acting solenoid with two PCM Solenoid channels.
 * This type of solenoid can be forward (Forward channel is active), reverse (Reverse channel is active),
 * or off (Neither of the two channels are active).
 */
class TDoubleSolenoid(forwardChannel: Int, reverseChannel: Int, module: Int? = null) : TSolenoid {

    private val wpiSolenoid: DoubleSolenoid =
            if (module == null) DoubleSolenoid(forwardChannel, reverseChannel)
            else DoubleSolenoid(module, forwardChannel, reverseChannel)

    // Set the solenoid to the desired position
    override var state: TSolenoid.State by Delegates.observable(TSolenoid.State.Off) { _, _, newValue ->
        when (newValue) {
            TSolenoid.State.Forward -> wpiSolenoid.set(DoubleSolenoid.Value.kForward)
            TSolenoid.State.Reverse -> wpiSolenoid.set(DoubleSolenoid.Value.kReverse)
            TSolenoid.State.Off -> wpiSolenoid.set(DoubleSolenoid.Value.kOff)
        }
    }
}
