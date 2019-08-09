/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import org.ghrobotics.lib.mathematics.units.centi
import org.ghrobotics.lib.mathematics.units.inch
import org.ghrobotics.lib.mathematics.units.meter
import org.ghrobotics.lib.mathematics.units.milli
import org.ghrobotics.lib.mathematics.units.millimeter
import org.junit.Test

class LengthTest {

    @Test
    fun testLength() {
        val one = 1.0.meter
        val two = 12.0.inch

        val three = one + two

        assert(three.meter epsilonEquals 1.3048)
    }

    @Test
    fun testPrefix() {
        val one = 1.0.meter
        val two = 100.centi.meter

        val three = one + two

        assert(three.millimeter epsilonEquals 2000.0)
    }

    @Test
    fun testScalar() {
        val one = 12.meter

        val two = one / 3.0
        val three = two * 3.0

        assert(two.meter epsilonEquals 4.0)
        assert(three.meter epsilonEquals 12.0)
    }

    @Test
    fun testToMetric() {
        val one = 40.inch

        val two = one.millimeter

        assert(two.toInt() == 1016)
    }

    @Test
    fun testFromMetric() {
        val one = 1016.milli.meter

        val two = one.inch

        assert(two.toInt() == 40)
    }
}