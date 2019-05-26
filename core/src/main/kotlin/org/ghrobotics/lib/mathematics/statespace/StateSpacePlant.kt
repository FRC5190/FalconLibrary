package org.ghrobotics.lib.mathematics.statespace

import org.ghrobotics.lib.mathematics.linalg.*

/**
 * A plant defined using state-space notation.
 *
 * A plant is a mathematical model of a system's dynamics.
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */

@Suppress("PropertyName")
class StateSpacePlant<States : `100`, Inputs : `100`, Outputs : `100`>(
    val coeffs: StateSpacePlantCoeffs<States, Inputs, Outputs>
) {

    private val states = coeffs.states
    private val outputs = coeffs.outputs

    val A = coeffs.A
    val B = coeffs.B
    val C = coeffs.C
    val D = coeffs.D

    var x: Vector<States> = zeros(states)
    var y: Vector<Outputs> = zeros(outputs)

    fun update(u: Matrix<Inputs, `1`>) {
        x = updateX(x, u)
        y = updateY(u)
    }

    fun updateX(x: Matrix<States, `1`>, u: Matrix<Inputs, `1`>): Matrix<States, `1`> {
        return A * x + B * u
    }

    fun updateY(u: Matrix<Inputs, `1`>): Matrix<Outputs, `1`> {
        return C * x + D * u
    }
}