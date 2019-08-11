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

class TimeTest {

    @Test
    fun testDivision() {
        val one = 45.days

        val two = one / 3

        assert(two.inDays() == 15.0)
    }

    @Test
    fun testAddition() {
        val one = 2.5.minutes
        val two = 360.seconds

        val three = one + two

        assert(three.inMinutes() epsilonEquals 8.5)
    }
}