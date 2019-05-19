package org.ghrobotics.lib.mathematics.statespace

import koma.matrix.Matrix
import koma.util.validation.validate

/**
 * A container for all the state-space controller coefficients.
 * @param numStates Number of states
 * @param numInputs Number of inputs
 * @param numOutputs Number of outputs
 * @param _KAndU Lambda that returns K, Kff, U, and U Min matrices
 */
class StateSpaceControllerCoeffs(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    _KAndU: () -> List<Matrix<Double>>
) {
    val KAndU = _KAndU()

    init {
        require(KAndU.size == 4)
    }

    val K = KAndU[0]
    val Kff = KAndU[1]
    val UMin = KAndU[2]
    val UMax = KAndU[3]

    init {
        validate {
            K("K") { numInputs x numStates }
            Kff("Kff") { numInputs x numStates }
            UMin("U Min") { numInputs x 1 }
            UMax("U Max") { numInputs x 1 }
        }
    }
}
