package org.ghrobotics.lib.mathematics.statespace

import koma.matrix.Matrix
import koma.util.validation.validate

/**
 * Container for state space observer coefficients
 * @param numStates Number of states
 * @param numInputs Number of inputs
 * @param numOutputs Number of outputs
 * @param _K Lambda that returns Kalman Gain Matrix
 */
class StateSpaceObserverCoeffs(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    _K: () -> Matrix<Double>
) {

    /**
     * Kalman gain matrix
     */
    val K = _K()

    init {
        validate {
            K("Kalman Gain Matrix") { numStates x numOutputs }
        }
    }
}