/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.derived.radian
import org.ghrobotics.lib.mathematics.units.derived.velocity
import org.ghrobotics.lib.mathematics.units.feet
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.kilo
import org.ghrobotics.lib.mathematics.units.lb
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.nativeunit.NativeUnitLengthModel
import org.ghrobotics.lib.mathematics.units.nativeunit.fromNativeUnitPosition
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnits
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100ms
import org.ghrobotics.lib.mathematics.units.nativeunit.nativeUnitsPer100msPerSecond
import org.ghrobotics.lib.mathematics.units.nativeunit.toNativeUnitAcceleration
import org.ghrobotics.lib.mathematics.units.nativeunit.toNativeUnitVelocity
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.second
import org.junit.Test

class UnitTest {

    private val settings = NativeUnitLengthModel(
        1440.nativeUnits,
        3.0.inch
    )

    @Test
    fun testNativeUnits() {
        val nativeUnits = 360.nativeUnits.fromNativeUnitPosition(settings)

        assert(nativeUnits.inch epsilonEquals 4.71238898038469)
    }

    @Test
    fun testVelocitySTU() {
        val one = 1.meter / 1.second

        val two = one.toNativeUnitVelocity(settings)

        val three = two.nativeUnitsPer100ms

        assert(three.toInt() == 300)
    }

    @Test
    fun testAccelerationSTU() {
        val one = 1.meter / 1.second / 1.second

        val two = one.toNativeUnitAcceleration(settings)

        assert(two.nativeUnitsPer100msPerSecond.toInt() == 300)
    }

    @Test
    fun testFeetToMeter() {
        val one = 1.feet

        assert(one.meter epsilonEquals 0.3048)
    }

    @Test
    fun testKgToPound() {
        val kg = 2.kilo.gram
        assert(kg.lb epsilonEquals 4.409248840367555)
    }

    @Test
    fun testUnboundedRotationUnits() {
        val speed = 250.radian.velocity
        assert(speed.value epsilonEquals 250.0)
    }
}