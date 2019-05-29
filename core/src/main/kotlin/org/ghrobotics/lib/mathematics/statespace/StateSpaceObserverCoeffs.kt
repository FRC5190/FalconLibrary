package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.*

data class StateSpaceObserverCoeffs<States : Num, Inputs : Num, Outputs : Num>(
    val K: Matrix<States, Outputs>
)