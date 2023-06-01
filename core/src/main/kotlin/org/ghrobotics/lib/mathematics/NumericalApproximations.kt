/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics

class NumericalApproximations private constructor() {
    companion object {
        tailrec fun newtonsMethod(
            numIterations: Int,
            x: Double,
            f: (x: Double) -> Double,
            fPrime: (x: Double) -> Double,
        ): Double =
            if (numIterations < 1) {
                x
            } else {
                newtonsMethod(numIterations - 1, x - f(x) / fPrime(x), f, fPrime)
            }
    }
}
