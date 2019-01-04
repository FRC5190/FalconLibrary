package org.ghrobotics.lib.simulation

import org.ghrobotics.lib.mathematics.units.SIValue
import org.ghrobotics.lib.mathematics.units.derivedunits.Velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.velocity
import org.ghrobotics.lib.mathematics.units.derivedunits.volt
import org.ghrobotics.lib.wrappers.FalconMotor

class SimFalconMotor<T : SIValue<T>>(
    type: T
) : FalconMotor<T> {

    override var percentOutput = 0.0
    override val voltageOutput = 0.volt
    override var velocity = type.createNew(0.0).velocity

    override fun setVelocityAndArbitraryFeedForward(velocity: Velocity<T>, arbitraryFeedForward: Double) {
        this.velocity = velocity
    }

}