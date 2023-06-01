/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.mathematics.statespace

import frc.team4069.keigen.Matrix
import frc.team4069.keigen.Num

data class StateSpaceObserverCoeffs<States : Num, Inputs : Num, Outputs : Num>(
    val K: Matrix<States, Outputs>,
)
