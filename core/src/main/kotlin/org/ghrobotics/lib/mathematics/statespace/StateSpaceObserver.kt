/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.`1`
import frc.team4069.keigen.Matrix
import frc.team4069.keigen.Num
import frc.team4069.keigen.Vector
import frc.team4069.keigen.zeros

/**
 * Luenberger observers combine predictions from a model and measurements to
 * give an estimate of the true system state.
 *
 * Luenberger observers use an L gain matrix to determine whether to trust the
 * model or measurements more. Kalman filter theory uses statistics to compute
 * an optimal L gain (alternatively called the Kalman gain, K) which minimizes
 * the sum of squares error in the state estimate.
 *
 * Luenberger observers run the prediction and correction steps simultaneously
 * while Kalman filters run them sequentially. To implement a discrete-time
 * Kalman filter as a Luenberger observer, use the following mapping:
 * <pre>C = H, L = A * K</pre>
 * (H is the measurement matrix).
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */
@Suppress("PrivatePropertyName")
class StateSpaceObserver<States : Num, Inputs : Num, Outputs : Num>(
    coeffs: StateSpaceObserverCoeffs<States, Inputs, Outputs>,
    private val plant: StateSpacePlant<States, Inputs, Outputs>,
) {

    private val states = plant.coeffs.states

    private val K = coeffs.K

    var xHat: Vector<States> = zeros(states)

    fun reset() {
        xHat = zeros(states)
    }

    fun predict(newU: Matrix<Inputs, `1`>) {
        xHat = plant.updateX(xHat, newU)
    }

    fun correct(u: Vector<Inputs>, y: Vector<Outputs>) {
        xHat += K * (y - plant.C * xHat - plant.D * u)
    }
}
