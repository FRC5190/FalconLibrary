package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.*

data class StateSpaceObserverCoeffs<States : `50`, Inputs : `50`, Outputs : `50`>(
    val K: Matrix<States, Outputs>
)