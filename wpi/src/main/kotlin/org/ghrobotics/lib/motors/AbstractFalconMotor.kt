/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.motors

import org.ghrobotics.lib.mathematics.units.SIKey

abstract class AbstractFalconMotor<K : SIKey> : FalconMotor<K> {

    override var useMotionProfileForPosition: Boolean = false

    override fun follow(motor: FalconMotor<*>): Boolean {
        TODO("Cross brand motor controller following not yet implemented!")
    }

}