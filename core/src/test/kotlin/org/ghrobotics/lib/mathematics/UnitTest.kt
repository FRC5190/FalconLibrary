/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derived.radians
import org.ghrobotics.lib.mathematics.units.derived.velocity
import org.ghrobotics.lib.mathematics.units.nativeunit.*
import org.ghrobotics.lib.mathematics.units.operations.div
import org.junit.Test

class UnitTest {

    private val settings = NativeUnitLengthModel(
        1440.nativeUnits,
        3.0.inches
    )

    @Test
    fun testNativeUnits() {
        val nativeUnits = 360.nativeUnits.fromNativeUnitPosition(settings)

        assert(nativeUnits.inInches() epsilonEquals 4.71238898038469)
    }

    @Test
    fun testVelocitySTU() {
        val one = 1.meters / 1.seconds

        val two = one.toNativeUnitVelocity(settings)

        val three = two.inNativeUnitsPer100ms()

        assert(three.toInt() == 300)
    }

    @Test
    fun testAccelerationSTU() {
        val one = 1.meters / 1.seconds / 1.seconds

        val two = one.toNativeUnitAcceleration(settings)

        assert(two.inNativeUnitsPer100msPerSecond().toInt() == 300)
    }

    @Test
    fun testFeetToMeter() {
        val one = 1.feet

        assert(one.inMeters() epsilonEquals 0.3048)
    }

    @Test
    fun testKgToPound() {
        val kg = 2.kilo.grams
        assert(kg.inLbs() epsilonEquals 4.409248840367555)
    }

    @Test
    fun testUnboundedRotationUnits() {
        val speed = 250.radians.velocity
        assert(speed.value epsilonEquals 250.0)
    }
}