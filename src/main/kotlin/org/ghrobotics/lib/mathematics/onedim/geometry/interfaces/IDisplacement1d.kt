package org.ghrobotics.lib.mathematics.onedim.geometry.interfaces

import org.ghrobotics.lib.mathematics.State
import org.ghrobotics.lib.mathematics.onedim.geometry.Displacement1d

interface IDisplacement1d<S> : State<S> {
    val displacement: Displacement1d
}
