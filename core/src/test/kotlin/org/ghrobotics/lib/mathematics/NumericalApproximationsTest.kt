/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

import org.junit.Assert
import org.junit.Test
import kotlin.math.pow

class NumericalApproximationsTest {
    @Test
    fun testNewtonsMethod1() {
        val f = { x: Double -> 18 - x.pow(2) }
        val fPrime = { x: Double -> -2 * x }
        val iteratons = 3
        val x1 = 4.1

        val approximatedZero = NumericalApproximations.newtonsMethod(iteratons, x1, f, fPrime)
        Assert.assertEquals(4.242640687, approximatedZero, kEpsilon)
    }

    @Test
    fun testNewtonsMethod2() {
        val f = { x: Double -> x.pow(3) + 4 * x + 8 }
        val fPrime = { x: Double -> 3 * x.pow(2) + 4 }
        val iteratons = 3
        val x1 = -1.3

        val approximatedZero = NumericalApproximations.newtonsMethod(iteratons, x1, f, fPrime)
        Assert.assertEquals(-1.364655608, approximatedZero, kEpsilon)
    }
}
