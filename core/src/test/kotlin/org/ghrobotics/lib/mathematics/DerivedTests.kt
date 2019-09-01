/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics


import org.ghrobotics.lib.mathematics.units.*
import org.ghrobotics.lib.mathematics.units.derived.feetPerMinute
import org.ghrobotics.lib.mathematics.units.derived.feetPerSecond
import org.ghrobotics.lib.mathematics.units.derived.volts
import org.ghrobotics.lib.mathematics.units.operations.div
import org.ghrobotics.lib.mathematics.units.operations.times
import org.junit.Test

class DerivedTests {

    @Test
    fun testVelocity() {
        val one = 5.meters
        val two = 2.seconds

        val three = one / two

        assert(three.value == 2.5)
    }

    @Test
    fun testVelocityAdjust() {
        val one = 5.meters
        val two = 2.seconds

        val three = one / two

        val four = three.feetPerMinute

        assert(four epsilonEquals 492.12598425196853)
    }

    @Test
    fun testAcceleration() {
        val one = 10.meters / 2.seconds / 4.seconds

        assert(one.value == 1.25)
    }

    @Test
    fun testAccelerationToVelocity() {
        val one = 10.meters / 1.6.seconds / 2.seconds
        val two = 5.seconds

        val three = one * two

        val four = three.feetPerSecond

        assert(four epsilonEquals 51.26312335958006)
    }

    @Test
    fun testVelocityToLength() {
        val one = 5.meters / 2.seconds
        val two = 6.seconds

        val three = one * two
        val four = three.inMeters()

        assert(four == 15.0)
    }

    @Test
    fun testVelocityAndAccelerationToTime() {
        val one = 22.meters / 2.seconds
        val two = 18.meters / 0.5.seconds / 4.seconds

        val three = one / two

        assert(three.inSeconds() epsilonEquals 1.2222222222222223)
    }

    @Test
    fun testAccelerationDividedByAcceleration() {
        val one = 33.meters / 1.seconds / 1.seconds
        val two = 22.meters / 2.seconds / 1.seconds

        val three = (one / two).unitlessValue

        assert(three == 3.0)
    }

    @Test
    fun testVelocityDividedByVelocity() {
        val one = 33.meters / 1.seconds
        val two = 22.meters / 2.seconds

        val three = (one / two).unitlessValue

        assert(three epsilonEquals 3.0)
    }

    @Test
    fun testVoltage() {
        val one = 1.volts
        val two = 5.amps

        val three = one * two

        assert(three.value epsilonEquals 5.0)
    }

    @Test
    fun testOhm() {
        val one = 2.volts
        val two = 5.amps

        val three = one / two

        assert(three.value epsilonEquals 0.4)
    }
}