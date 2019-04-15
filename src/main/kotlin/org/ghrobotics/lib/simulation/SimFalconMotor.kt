package org.ghrobotics.lib.simulation

import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.motors.FalconEncoder
import org.ghrobotics.lib.motors.FalconMotor

class SimFalconMotor<T : SIUnit<T>> : FalconMotor<T> {

    var velocity = 0.0
    override val voltageOutput = 0.0

    override val encoder = object : FalconEncoder<T> {
        override val velocity: Double get() = rawVelocity
        override val position: Double get() = rawPosition
        override val rawVelocity: Double get() = this@SimFalconMotor.velocity
        override val rawPosition: Double get() = 0.0

        override fun resetPosition(newPosition: Double) {}
    }

    override var outputInverted: Boolean
        get() = TODO("not implemented")
        set(value) {}

    override var brakeMode: Boolean
        get() = TODO("not implemented")
        set(value) {}

    override fun follow(motor: FalconMotor<*>): Boolean {
        TODO("not implemented")
    }

    override fun setVoltage(voltage: Double, arbitraryFeedForward: Double) {
        TODO("not implemented")
    }

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: Double) {
        TODO("not implemented")
    }

    override fun setVelocity(velocity: Double, arbitraryFeedForward: Double) {
        this.velocity = velocity
    }

    override fun setPosition(position: Double, arbitraryFeedForward: Double) {
        TODO("not implemented")
    }

    override fun setNeutral() {
        velocity = 0.0
    }

    override var voltageCompSaturation: Double
        get() = TODO("not implemented")
        set(value) {TODO("not implemented")}

    override var motionProfileCruiseVelocity: Double
        get() = TODO("not implemented")
        set(value) {TODO("not implemented")}

    override var motionProfileAcceleration: Double
        get() = TODO("not implemented")
        set(value) {TODO("not implemented")}

    override var useMotionProfileForPosition = false

}