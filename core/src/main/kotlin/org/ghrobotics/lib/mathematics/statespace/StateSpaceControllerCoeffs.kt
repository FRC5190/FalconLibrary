package org.ghrobotics.lib.mathematics.statespace

import org.ghrobotics.lib.mathematics.linalg.`100`
import org.ghrobotics.lib.mathematics.linalg.Matrix
import org.ghrobotics.lib.mathematics.linalg.Vector

data class StateSpaceControllerCoeffs<States : `100`, Inputs : `100`, Outputs : `100`>(
    val K: Matrix<Inputs, States>,
    val Kff: Matrix<Inputs, States>,
    val UMin: Vector<Inputs>,
    val UMax: Vector<Inputs>
)
