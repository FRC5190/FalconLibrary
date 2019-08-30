/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj2.command.SubsystemBase

abstract class FalconSubsystem : SubsystemBase() {
    open fun lateInit() {}
    open fun autoReset() {}
    open fun teleopReset() {}
    open fun setNeutral() {}
}