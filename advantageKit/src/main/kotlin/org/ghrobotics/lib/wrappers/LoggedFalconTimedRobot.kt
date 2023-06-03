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
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandScheduler
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.FalconSubsystemHandler
import org.ghrobotics.lib.subsystems.SensorlessCompatibleSubsystem
import org.littletonrobotics.junction.LoggedRobot

abstract class LoggedFalconTimedRobot {
    enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST,
        SIMULATION,
    }

    var currentMode = Mode.NONE
        private set

    private val sensorlessReadySystems = arrayListOf<SensorlessCompatibleSubsystem>()
    var sensorlessModeActive = false
        protected set

    protected val wrappedValue = WpiTimedRobot()

    protected inner class WpiTimedRobot : LoggedRobot() {

        private val kLanguage_Kotlin = 6

        init {
            HAL.report(FRCNetComm.tResourceType.kResourceType_Language, kLanguage_Kotlin)
        }

        override fun robotInit() {
            currentMode = LoggedFalconTimedRobot.Mode.NONE
            this@LoggedFalconTimedRobot.robotInit()
            FalconSubsystemHandler.lateInit()
            LiveWindow.disableAllTelemetry()
        }

        override fun autonomousInit() {
            currentMode = LoggedFalconTimedRobot.Mode.AUTONOMOUS
            this@LoggedFalconTimedRobot.autonomousInit()
            FalconSubsystemHandler.autoReset()
        }

        override fun teleopInit() {
            currentMode = LoggedFalconTimedRobot.Mode.TELEOP
            this@LoggedFalconTimedRobot.teleopInit()
            FalconSubsystemHandler.teleopReset()
        }

        override fun disabledInit() {
            currentMode = LoggedFalconTimedRobot.Mode.DISABLED
            this@LoggedFalconTimedRobot.disabledInit()
            FalconSubsystemHandler.setNeutral()
        }

        override fun testInit() {
            currentMode = LoggedFalconTimedRobot.Mode.TEST
            this@LoggedFalconTimedRobot.testInit()
        }

        override fun robotPeriodic() {
            this@LoggedFalconTimedRobot.robotPeriodic()
            CommandScheduler.getInstance().run()
        }

        override fun autonomousPeriodic() {
            this@LoggedFalconTimedRobot.autonomousPeriodic()
        }

        override fun teleopPeriodic() {
            this@LoggedFalconTimedRobot.teleopPeriodic()
        }

        override fun disabledPeriodic() {
            this@LoggedFalconTimedRobot.disabledPeriodic()
        }

        override fun simulationInit() {
            currentMode = LoggedFalconTimedRobot.Mode.SIMULATION
            this@LoggedFalconTimedRobot.simulationInit()
        }

        override fun simulationPeriodic() {
            this@LoggedFalconTimedRobot.simulationPeriodic()
        }
    }

    protected open fun robotInit() {}
    protected open fun autonomousInit() {}
    protected open fun teleopInit() {}
    protected open fun disabledInit() {}
    protected open fun testInit() {}

    protected open fun robotPeriodic() {}
    protected open fun autonomousPeriodic() {}
    protected open fun teleopPeriodic() {}
    protected open fun disabledPeriodic() {}

    protected open fun simulationPeriodic() {}
    protected open fun simulationInit() {}
    protected fun getSubsystemChecks(): Command {
        return FalconSubsystemHandler.testCommand
    }

    open operator fun FalconSubsystem.unaryPlus() {
        FalconSubsystemHandler.add(this)
        if (this is SensorlessCompatibleSubsystem) {
            sensorlessReadySystems.add(this)
        }
    }

    fun disableClosedLoopControl() {
        sensorlessReadySystems.forEach { it.disableClosedLoopControl() }
        sensorlessModeActive = true
    }

    fun enableClosedLoopControl() {
        sensorlessReadySystems.forEach { it.enableClosedLoopControl() }
        sensorlessModeActive = false
    }

    fun setUseTiming(useTiming: Boolean) {
        wrappedValue.setUseTiming(useTiming)
    }

    fun start() {
        RobotBase.startRobot { wrappedValue }
    }
}
