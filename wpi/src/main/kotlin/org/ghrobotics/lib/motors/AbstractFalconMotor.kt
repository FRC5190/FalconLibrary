package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.SIKey

abstract class AbstractFalconMotor<K : SIKey> : FalconMotor<K> {

    override var useMotionProfileForPosition: Boolean = false

    override fun follow(motor: FalconMotor<*>): Boolean {
        TODO("Cross brand motor controller following not yet implemented!")
    }

}