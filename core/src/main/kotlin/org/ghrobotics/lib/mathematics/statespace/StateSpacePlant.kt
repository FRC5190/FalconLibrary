package org.ghrobotics.lib.mathematics.statespace

import koma.matrix.Matrix
import koma.util.validation.validate
import koma.zeros

/**
 * A plant defined using state-space notation.
 *
 * A plant is a mathematical model of a system's dynamics.
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */

class StateSpacePlant(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    plantCoeffs: StateSpacePlantCoeffs
) {

    constructor(plantCoeffs: StateSpacePlantCoeffs) : this(
        plantCoeffs.numStates,
        plantCoeffs.numInputs,
        plantCoeffs.numOutputs,
        plantCoeffs
    )

    val A = plantCoeffs.A
    val B = plantCoeffs.B
    val C = plantCoeffs.C
    val D = plantCoeffs.D

    var x = zeros(numStates, 1)
        set(value) {
            validate { value("X") { numStates x 1 } }
            field = value
        }

    var y = zeros(numOutputs, 1)
        set(value) {
            validate { value("Y") { numOutputs x 1 } }
            field = value
        }

    fun update(u: Matrix<Double>) {
        validate { u("U") { numInputs x 1 } }
        x = updateX(x, u)
        y = updateY(u)
    }

    fun updateX(x: Matrix<Double>, u: Matrix<Double>): Matrix<Double> {
        validate {
            x("X") { numStates x 1 }
            u("U") { numInputs x 1 }
        }
        return A * x + B * u
    }

    fun updateY(u: Matrix<Double>): Matrix<Double> {
        validate { u("U") { numInputs x 1 } }
        return C * x + D * u
    }
}