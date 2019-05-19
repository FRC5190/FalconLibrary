package org.ghrobotics.lib.mathematics.statespace

import koma.matrix.Matrix
import koma.util.validation.validate

/**
 * A container for all the state-space plant coefficients.
 * @param numStates Number of states
 * @param numInputs Number of inputs
 * @param numOutputs Number of outputs
 * @param _ABCD Lambda that returns array of A, B, C, D matrices
 */
class StateSpacePlantCoeffs(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    _ABCD: () -> List<Matrix<Double>>
) {
    private val ABCD = _ABCD()

    init {
        require(ABCD.size == 4)
    }

    val A = ABCD[0]
    val B = ABCD[1]
    val C = ABCD[2]
    val D = ABCD[3]

    init {
        validate {
            A("A") { numStates x numStates }
            B("B") { numStates x numInputs }
            C("C") { numOutputs x numStates }
            D("D") { numOutputs x numInputs }
        }
    }

}
