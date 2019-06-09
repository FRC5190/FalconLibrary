package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.experimental.command.SendableSubsystemBase

abstract class FalconSubsystem : SendableSubsystemBase() {
    open fun lateInit() {}
    open fun autoReset() {}
    open fun teleopReset() {}
    open fun setNeutral() {}
}