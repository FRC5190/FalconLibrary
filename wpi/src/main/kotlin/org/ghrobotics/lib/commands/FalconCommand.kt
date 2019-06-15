package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.experimental.command.SendableCommandBase
import edu.wpi.first.wpilibj.experimental.command.Subsystem

abstract class FalconCommand(vararg requirements: Subsystem) : SendableCommandBase() {
    init {
        addRequirements(*requirements)
    }
}