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
 * A plant defined using state-space notation.
 *
 * A plant is a mathematical model of a system's dynamics.
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */

@Suppress("PropertyName")
class StateSpacePlant<States : Num, Inputs : Num, Outputs : Num>(
    val coeffs: StateSpacePlantCoeffs<States, Inputs, Outputs>,
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

    fun updateX(x: Vector<States>, u: Vector<Inputs>): Vector<States> {
        return A * x + B * u
    }

    fun updateY(u: Vector<Inputs>): Vector<Outputs> {
        return C * x + D * u
    }
}
