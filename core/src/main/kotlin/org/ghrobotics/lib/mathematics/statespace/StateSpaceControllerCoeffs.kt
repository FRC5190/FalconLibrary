package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.*

data class StateSpaceControllerCoeffs<States : Num, Inputs : Num, Outputs : Num>(
    val K: Matrix<Inputs, States>,
    val Kff: Matrix<Inputs, States>,
    val UMin: Vector<Inputs>,
    val UMax: Vector<Inputs>
)
