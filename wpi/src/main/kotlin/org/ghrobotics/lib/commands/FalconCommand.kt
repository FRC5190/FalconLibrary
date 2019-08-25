/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.frc2.command.SendableCommandBase
import edu.wpi.first.wpilibj.frc2.command.Subsystem

abstract class FalconCommand(vararg requirements: Subsystem) : SendableCommandBase() {
    init {
        addRequirements(*requirements)
    }
}