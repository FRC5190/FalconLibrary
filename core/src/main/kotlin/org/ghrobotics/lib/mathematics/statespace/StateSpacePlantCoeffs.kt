package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.*
data class StateSpacePlantCoeffs<States : `50`, Inputs : `50`, Outputs : `50`>(
    val states: Nat<States>, val inputs: Nat<Inputs>, val outputs: Nat<Outputs>,
    val A: Matrix<States, States>,
    val B: Matrix<States, Inputs>,
    val C: Matrix<Outputs, States>,
    val D: Matrix<Outputs, Inputs>
)
