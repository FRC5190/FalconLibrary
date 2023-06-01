/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.Num
import frc.team4069.keigen.Vector
import frc.team4069.keigen.get
import frc.team4069.keigen.set
import frc.team4069.keigen.zeros

/**
 * Contains the controller coefficients and logic for a state-space controller.
 *
 * State-space controllers generally use the control law u = -Kx. The
 * feedforward used is u_ff = K_ff * (r_k+1 - A * r_k).
 *
 * For more on the underlying math, read
 * https://file.tavsys.net/control/state-space-guide.pdf.
 */
@Suppress("PrivatePropertyName")
class StateSpaceController<States : Num, Inputs : Num, Outputs : Num>(
    coeffs: StateSpaceControllerCoeffs<States, Inputs, Outputs>,
    private val plant: StateSpacePlant<States, Inputs, Outputs>,
) {

    private val states = plant.coeffs.states
    private val inputs = plant.coeffs.inputs

    private val K = coeffs.K
    private val Kff = coeffs.Kff
    private val UMin = coeffs.UMin
    private val UMax = coeffs.UMax

    private var isEnabled = false

    var r: Vector<States> = zeros(states)
    var u: Vector<Inputs> = zeros(inputs)

    fun enable() {
        isEnabled = true
    }

    fun disable() {
        isEnabled = false
        u = zeros(inputs)
    }

    fun reset() {
        r = zeros(states)
        u = zeros(inputs)
    }

    fun update(x: Vector<States>) {
        u = K * (r - x) + Kff * (r - plant.A * r)
        capU()
    }

    fun update(nextR: Vector<States>, x: Vector<States>) {
        u = K * (r - x) + Kff * (nextR - plant.A * r)
        r = nextR
        capU()
    }

    private fun capU() {
        for (i in 0 until inputs.i) {
            u[i] = u[i].coerceIn(UMin[i], UMax[i])
        }
    }
}
