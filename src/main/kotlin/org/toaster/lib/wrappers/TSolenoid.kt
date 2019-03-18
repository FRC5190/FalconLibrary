package org.toaster.lib.wrappers

import edu.wpi.first.wpilibj.DoubleSolenoid
import edu.wpi.first.wpilibj.Solenoid

interface TSolenoid {
    enum class State {
        Forward,
        Reverse,
        Off
    }

    fun set(state: State)
    fun get() : State
}

class TSingleSolenoid(channel: Int, module: Int?) : TSolenoid {
    private val wpiSolenoid: Solenoid = if(module == null) Solenoid(channel) else Solenoid(module, channel)

    override fun set(state: TSolenoid.State) = when(state){
        TSolenoid.State.Forward -> wpiSolenoid.set(true)
        TSolenoid.State.Reverse -> wpiSolenoid.set(false)
        else -> Unit
    }

    override fun get(): TSolenoid.State = when(wpiSolenoid.get()) {
        true -> TSolenoid.State.Forward
        false -> TSolenoid.State.Reverse
    }
}

class TDoubleSolenoid(forwardChannel: Int, reverseChannel: Int, module: Int?) : TSolenoid {
    private val wpiSolenoid: DoubleSolenoid =
            if(module == null) DoubleSolenoid(forwardChannel, reverseChannel)
            else DoubleSolenoid(module, forwardChannel, reverseChannel)

    override fun set(state: TSolenoid.State) = when(state){
        TSolenoid.State.Forward -> wpiSolenoid.set(DoubleSolenoid.Value.kForward)
        TSolenoid.State.Reverse -> wpiSolenoid.set(DoubleSolenoid.Value.kReverse)
        TSolenoid.State.Off -> wpiSolenoid.set(DoubleSolenoid.Value.kOff)
    }

    override fun get(): TSolenoid.State = when(wpiSolenoid.get()){
        DoubleSolenoid.Value.kForward -> TSolenoid.State.Forward
        DoubleSolenoid.Value.kReverse -> TSolenoid.State.Reverse
        DoubleSolenoid.Value.kOff -> TSolenoid.State.Off
        else -> TSolenoid.State.Off
    }
}