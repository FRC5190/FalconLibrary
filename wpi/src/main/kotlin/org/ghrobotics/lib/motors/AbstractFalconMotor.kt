package org.ghrobotics.lib.motors

abstract class AbstractFalconMotor<T : SIUnit<T>> : FalconMotor<T> {

    override var useMotionProfileForPosition: Boolean = false

    override fun follow(motor: FalconMotor<*>): Boolean {
        TODO("Cross brand motor controller following not yet implemented!")
    }

}