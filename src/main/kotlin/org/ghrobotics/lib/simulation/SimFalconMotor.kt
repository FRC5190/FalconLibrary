package org.ghrobotics.lib.simulation

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.wrappers.FalconMotor

class SimFalconMotor<T : SIValue<T>> : FalconMotor<T> {

    override var percentOutput = 0.0
    override val voltageOutput = 0.0
    override var velocity = 0.0

    override fun setVelocityAndArbitraryFeedForward(velocity: Double, arbitraryFeedForward: Double) {
        this.velocity = velocity
    }

}