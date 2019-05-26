package org.ghrobotics.lib.mathematics.statespace

import org.ghrobotics.lib.mathematics.linalg.`100`
import org.ghrobotics.lib.mathematics.linalg.Matrix

data class StateSpaceObserverCoeffs<States : `100`, Inputs : `100`, Outputs : `100`>(
    val K: Matrix<States, Outputs>
)