package org.ghrobotics.lib.wrappers

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.TimedRobot
import edu.wpi.first.wpilibj.experimental.command.CommandScheduler
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.FalconSubsystemHandler
import org.ghrobotics.lib.subsystems.EmergencyHandleable

abstract class FalconTimedRobot {

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
            this@FalconTimedRobot.robotInit()
            FalconSubsystemHandler.lateInit()
            LiveWindow.disableAllTelemetry()
        }

        override fun autonomousInit() {
            this@FalconTimedRobot.autonomousInit()
            FalconSubsystemHandler.autoReset()
        }

        override fun teleopInit() {
            this@FalconTimedRobot.teleopInit()
            FalconSubsystemHandler.teleopReset()
        }

        override fun disabledInit() {
            this@FalconTimedRobot.disabledInit()
            FalconSubsystemHandler.setNeutral()
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
