/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.wrappers

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj2.command.CommandScheduler
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.FalconSubsystemHandler
import org.ghrobotics.lib.subsystems.EmergencyHandleable

abstract class FalconTimedRobot {

    enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST
    }

    var currentMode = Mode.NONE
        private set

    private val emergencyReadySystems = arrayListOf<EmergencyHandleable>()
    var emergencyActive = false
        protected set

    protected val wrappedValue = WpiTimedRobot()

    protected inner class WpiTimedRobot : TimedRobot() {

        private val kLanguage_Kotlin = 6

        init {
            HAL.report(FRCNetComm.tResourceType.kResourceType_Language, kLanguage_Kotlin)
        }

        override fun robotInit() {
            currentMode = FalconTimedRobot.Mode.NONE
            this@FalconTimedRobot.robotInit()
            FalconSubsystemHandler.lateInit()
            LiveWindow.disableAllTelemetry()
        }

        override fun autonomousInit() {
            currentMode = FalconTimedRobot.Mode.AUTONOMOUS
            this@FalconTimedRobot.autonomousInit()
            FalconSubsystemHandler.autoReset()
        }

        override fun teleopInit() {
            currentMode = FalconTimedRobot.Mode.TELEOP
            this@FalconTimedRobot.teleopInit()
            FalconSubsystemHandler.teleopReset()
        }

        override fun disabledInit() {
            currentMode = FalconTimedRobot.Mode.DISABLED
            this@FalconTimedRobot.disabledInit()
            FalconSubsystemHandler.setNeutral()
        }

        override fun testInit() {
            currentMode = FalconTimedRobot.Mode.TEST
            this@FalconTimedRobot.testInit()
        }

        override fun robotPeriodic() {
            this@FalconTimedRobot.robotPeriodic()
            CommandScheduler.getInstance().run()
        }

        override fun autonomousPeriodic() {
            this@FalconTimedRobot.autonomousPeriodic()
        }

        override fun teleopPeriodic() {
            this@FalconTimedRobot.teleopPeriodic()
        }

        override fun disabledPeriodic() {
            this@FalconTimedRobot.disabledPeriodic()
        }
    }

    protected open fun robotInit() {}
    protected open fun autonomousInit() {}
    protected open fun teleopInit() {}
    protected open fun disabledInit() {}
    private fun testInit() {}

    protected open fun robotPeriodic() {}
    protected open fun autonomousPeriodic() {}
    protected open fun teleopPeriodic() {}
    protected open fun disabledPeriodic() {}

    open operator fun FalconSubsystem.unaryPlus() {
        FalconSubsystemHandler.add(this)
        if (this is EmergencyHandleable) {
            emergencyReadySystems.add(this)
        }
    }

    fun activateEmergency() {
        emergencyReadySystems.forEach { it.activateEmergency() }
        emergencyActive = true
    }

    fun recoverFromEmergency() {
        emergencyReadySystems.forEach { it.recoverFromEmergency() }
        emergencyActive = false
    }

    fun start() {
        RobotBase.startRobot { wrappedValue }
    }
}
