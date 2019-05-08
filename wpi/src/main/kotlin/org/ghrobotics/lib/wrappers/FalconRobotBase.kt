package org.ghrobotics.lib.wrappers

import edu.wpi.first.hal.FRCNetComm
import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.shuffleboard.Shuffleboard
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.SubsystemHandler

const val kLanguageKotlin = 6

@Deprecated("Use FalconRobot instead")
abstract class FalconRobotBase : RobotBase() {

    companion object {
        var DEBUG = true
            private set
        @Suppress("LateinitUsage")
        lateinit var INSTANCE: FalconRobotBase
            private set
    }

    init {
        @Suppress("LeakingThis")
        INSTANCE = this
        DEBUG = false
    }

    enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST;
    }

    var currentMode = Mode.NONE
        private set

    // Main Robot Code

    var initialized = false
        private set

    protected abstract fun initialize()

    protected open fun periodic() {}

    @Suppress("ComplexMethod")
    override fun startCompetition() {
        HAL.report(FRCNetComm.tResourceType.kResourceType_Language, kLanguageKotlin)
        LiveWindow.setEnabled(false)

        initialize()
        SubsystemHandler.lateInit()
        initialized = true
        // Start up the default command
        println("[Robot] Initialized")

        // Tell the DS that the robot is ready to be enabled
        HAL.observeUserProgramStarting()

        while (true) {
            // Wait for new data to arrive
            m_ds.waitForData()

            val newMode = when {
                isDisabled -> Mode.DISABLED
                isAutonomous -> Mode.AUTONOMOUS
                isOperatorControl -> Mode.TELEOP
                isTest -> Mode.TEST
                else -> TODO("Robot in invalid mode!")
            }

            if (newMode != currentMode) {
                when (newMode) {
                    Mode.AUTONOMOUS -> SubsystemHandler.autoReset()
                    Mode.TELEOP -> SubsystemHandler.teleopReset()
                    Mode.DISABLED -> SubsystemHandler.zeroOutputs()
                    else -> {
                    }
                }
            }

            currentMode = newMode

            // Report robot state
            when (newMode) {
                Mode.DISABLED -> HAL.observeUserProgramDisabled()
                Mode.AUTONOMOUS -> HAL.observeUserProgramAutonomous()
                Mode.TELEOP -> HAL.observeUserProgramTeleop()
                Mode.TEST -> HAL.observeUserProgramTest()
                Mode.NONE -> throw IllegalStateException("Mode cannot be NONE.")
            }

            // Update Values
            SmartDashboard.updateValues()
            Shuffleboard.update()
            // LiveWindow.updateValues()

            // Update Commands
            Scheduler.getInstance().run()

            periodic()
        }
    }

    // Helpers
    protected fun addToSubsystemHandler(subsystem: FalconSubsystem) = SubsystemHandler.addSubsystem(subsystem)
    protected open operator fun FalconSubsystem.unaryPlus() = addToSubsystemHandler(this)

}
