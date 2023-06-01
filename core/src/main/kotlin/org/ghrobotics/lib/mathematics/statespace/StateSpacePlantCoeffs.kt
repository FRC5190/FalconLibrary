/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.Matrix
import frc.team4069.keigen.Nat
import frc.team4069.keigen.Num

data class StateSpacePlantCoeffs<States : Num, Inputs : Num, Outputs : Num>(
    val states: Nat<States>,
    val inputs: Nat<Inputs>,
    val outputs: Nat<Outputs>,
    val A: Matrix<States, States>,
    val B: Matrix<States, Inputs>,
    val C: Matrix<Outputs, States>,
    val D: Matrix<Outputs, Inputs>,
)
