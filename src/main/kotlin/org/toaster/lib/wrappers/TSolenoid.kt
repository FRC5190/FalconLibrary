package org.toaster.lib.wrappers

import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Solenoid

/**
 * Interface for both double- and single-solenoids.
 */
interface TSolenoid {
    enum class State {
        Forward,
        Reverse,
        Off
    }

    fun set(state: State)
    fun get() : State
}

/**
 * Single-acting solenoid with only one PCM Solenoid channel.
 * This type of solenoid can only be on or off.
 */
class TSingleSolenoid(channel: Int, module: Int?) : TSolenoid {
    private val wpiSolenoid: Solenoid = if(module == null) Solenoid(channel) else Solenoid(module, channel)

    // Set the solenoid's position. Forward -> true, Reverse -> false
    // If the solenoid is set to 'Off', the state is not changed.
    override fun set(state: TSolenoid.State) = when(state){
        TSolenoid.State.Forward -> wpiSolenoid.set(true)
        TSolenoid.State.Reverse -> wpiSolenoid.set(false)
        else -> Unit
    }

    // Get the solenoid's position. true -> forward, false -> reverse. Will never return Off.
    override fun get(): TSolenoid.State = when(wpiSolenoid.get()) {
        true -> TSolenoid.State.Forward
        false -> TSolenoid.State.Reverse
    }
}

/**
 * Double-acting solenoid with two PCM Solenoid channels.
 * This type of solenoid can be forward (Forward channel is active), reverse (Reverse channel is active),
 * or off (Neither of the two channels are active).
 */
class TDoubleSolenoid(forwardChannel: Int, reverseChannel: Int, module: Int?) : TSolenoid {
    private val wpiSolenoid: DoubleSolenoid =
            if(module == null) DoubleSolenoid(forwardChannel, reverseChannel)
            else DoubleSolenoid(module, forwardChannel, reverseChannel)

    // Set the solenoid to the desired position
    override fun set(state: TSolenoid.State) = when(state){
        TSolenoid.State.Forward -> wpiSolenoid.set(DoubleSolenoid.Value.kForward)
        TSolenoid.State.Reverse -> wpiSolenoid.set(DoubleSolenoid.Value.kReverse)
        TSolenoid.State.Off -> wpiSolenoid.set(DoubleSolenoid.Value.kOff)
    }

    // Get the solenoid's position. If the position is null, return Off.
    override fun get(): TSolenoid.State = when(wpiSolenoid.get()){
        DoubleSolenoid.Value.kForward -> TSolenoid.State.Forward
        DoubleSolenoid.Value.kReverse -> TSolenoid.State.Reverse
        DoubleSolenoid.Value.kOff -> TSolenoid.State.Off
        else -> TSolenoid.State.Off
    }
}