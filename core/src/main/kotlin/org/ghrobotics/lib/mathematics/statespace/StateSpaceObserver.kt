package org.ghrobotics.lib.mathematics.statespace

import koma.matrix.Matrix
import koma.util.validation.validate
import koma.zeros

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
class StateSpaceObserver(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    observerCoeffs: StateSpaceObserverCoeffs, val plant: StateSpacePlant
) {

    constructor(observerCoeffs: StateSpaceObserverCoeffs, plant: StateSpacePlant) : this(
        observerCoeffs.numStates,
        observerCoeffs.numInputs,
        observerCoeffs.numOutputs,
        observerCoeffs,
        plant
    )

    val K = observerCoeffs.K

    var xHat = zeros(numStates, 1)
        set(value) {
            validate { value("xhat") { numStates x 1 } }
            field = value
        }

    fun reset() {
        xHat = zeros(numStates, 1)
    }

    fun predict(newU: Matrix<Double>) {
        validate { newU("U") { numInputs x 1 } }
        xHat = plant.updateX(xHat, newU)
    }

    fun correct(u: Matrix<Double>, y: Matrix<Double>) {
        validate {
            u("U") { numInputs x 1 }
            y("y") { numOutputs x 1 }
        }
        xHat += K * (y - plant.C * xHat - plant.D * u)
    }
}