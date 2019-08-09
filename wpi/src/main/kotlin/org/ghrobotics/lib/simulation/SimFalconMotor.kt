/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.simulation

import org.ghrobotics.lib.mathematics.units.SIKey
import org.ghrobotics.lib.mathematics.units.SIUnit
import org.ghrobotics.lib.mathematics.units.derived.Acceleration
import org.ghrobotics.lib.mathematics.units.derived.Velocity
import org.ghrobotics.lib.mathematics.units.derived.Volt
import org.ghrobotics.lib.mathematics.units.derived.volt
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnit
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitVelocity
import org.ghrobotics.lib.motors.FalconEncoder
import org.ghrobotics.lib.motors.FalconMotor

class SimFalconMotor<K : SIKey> : FalconMotor<K> {

    var velocity = SIUnit<Velocity<K>>(0.0)
    override val voltageOutput = 0.0.volt

    override val encoder = object : FalconEncoder<K> {
        override val velocity: SIUnit<Velocity<K>> get() = this@SimFalconMotor.velocity
        override val position: SIUnit<K> = SIUnit(0.0)
        override val rawVelocity: SIUnit<NativeUnitVelocity> get() = SIUnit(velocity.value)
        override val rawPosition: SIUnit<NativeUnit> get() = SIUnit(position.value)

        override fun resetPosition(newPosition: SIUnit<K>) {}
        override fun resetPositionRaw(newPosition: SIUnit<NativeUnit>) {}
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

    override fun setVoltage(voltage: SIUnit<Volt>, arbitraryFeedForward: SIUnit<Volt>) {
        TODO("not implemented")
    }

    override fun setDutyCycle(dutyCycle: Double, arbitraryFeedForward: SIUnit<Volt>) {
        TODO("not implemented")
    }

    override fun setVelocity(velocity: SIUnit<Velocity<K>>, arbitraryFeedForward: SIUnit<Volt>) {
        this.velocity = velocity
    }

    override fun setPosition(position: SIUnit<K>, arbitraryFeedForward: SIUnit<Volt>) {
        TODO("not implemented")
    }

    override fun setNeutral() {
        velocity = SIUnit(0.0)
    }

    override var voltageCompSaturation: SIUnit<Volt>
        get() = TODO("not implemented")
        set(value) {
            TODO("not implemented")
        }

    override var motionProfileCruiseVelocity: SIUnit<Velocity<K>>
        get() = TODO("not implemented")
        set(value) {
            TODO("not implemented")
        }

    override var motionProfileAcceleration: SIUnit<Acceleration<K>>
        get() = TODO("not implemented")
        set(value) {
            TODO("not implemented")
        }

    override var useMotionProfileForPosition = false

}