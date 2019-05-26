package org.ghrobotics.lib.mathematics.statespace

import org.ghrobotics.lib.mathematics.linalg.`100`
import org.ghrobotics.lib.mathematics.linalg.Matrix
import org.ghrobotics.lib.mathematics.linalg.Nat

data class StateSpacePlantCoeffs<States : `100`, Inputs : `100`, Outputs : `100`>(
    val states: Nat<States>, val inputs: Nat<Inputs>, val outputs: Nat<Outputs>,
    val A: Matrix<States, States>,
    val B: Matrix<States, Inputs>,
    val C: Matrix<Outputs, States>,
    val D: Matrix<Outputs, Inputs>
)
