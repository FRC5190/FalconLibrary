package org.ghrobotics.lib.mathematics.statespace

import koma.extensions.get
import koma.extensions.set
import koma.matrix.Matrix
import koma.util.validation.validate
import koma.zeros

/**
 * Contains the controller coefficients and logic for a state-space controller.
 *
 * State-space controllers generally use the control law u = -Kx. The
 * feedforward used is u_ff = K_ff * (r_k+1 - A * r_k).
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */
class StateSpaceController(
    val numStates: Int, val numInputs: Int, val numOutputs: Int,
    val controllerCoeffs: StateSpaceControllerCoeffs, val plant: StateSpacePlant
) {

    constructor(controllerCoeffs: StateSpaceControllerCoeffs, plant: StateSpacePlant) : this(
        controllerCoeffs.numStates,
        controllerCoeffs.numInputs,
        controllerCoeffs.numOutputs,
        controllerCoeffs,
        plant
    )

    val K = controllerCoeffs.K
    val Kff = controllerCoeffs.Kff

    private var isEnabled = false

    var r = zeros(numStates, 1)
        set(value) {
            validate { value("r") { numStates x 1 } }
            field = value
        }

    var u = zeros(numInputs, 1)
        set(value) {
            validate { value("u") { numInputs x 1 } }
            field = value
        }

    fun enable() {
        isEnabled = true
    }

    fun disable() {
        isEnabled = false
        u = zeros(numInputs, 1)
    }

    fun reset() {
        r = zeros(numStates, 1)
        u = zeros(numInputs, 1)
    }

    fun update(x: Matrix<Double>) {
        validate { x("X") { numStates x 1 } }
        u = K * (r - x) + Kff * (r - plant.A * r)
        capU()
    }

    fun update(nextR: Matrix<Double>, x: Matrix<Double>) {
        validate {
            x("X") { numStates x 1 }
            nextR("r") { numStates x 1 }
        }
        u = K * (r - x) + Kff * (nextR - plant.A - r)
        r = nextR
        capU()
    }

    private fun capU() {
        for (i in 0 until numInputs) {
            u[i, 0] = u[i, 0].coerceIn(controllerCoeffs.UMin[i, 0], controllerCoeffs.UMax[i, 0])
        }
    }

}