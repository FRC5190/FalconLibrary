/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.*
import org.junit.Test

class LengthTest {

    @Test
    fun testLength() {
        val one = 1.0.meters
        val two = 12.0.inches

        val three = one + two

        assert(three.inMeters() epsilonEquals 1.3048)
    }

    @Test
    fun testPrefix() {
        val one = 1.0.meters
        val two = 100.centi.meters

        val three = one + two

        assert(three.inMillimeters() epsilonEquals 2000.0)
    }

    @Test
    fun testScalar() {
        val one = 12.meters

        val two = one / 3.0
        val three = two * 3.0

        assert(two.inMeters() epsilonEquals 4.0)
        assert(three.inMeters() epsilonEquals 12.0)
    }

    @Test
    fun testToMetric() {
        val one = 40.inches

        val two = one.inMillimeters()

        assert(two.toInt() == 1016)
    }

    @Test
    fun testFromMetric() {
        val one = 1016.milli.meters

        val two = one.inInches()

        assert(two.toInt() == 40)
    }
}