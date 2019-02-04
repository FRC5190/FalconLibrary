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
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.Source
import kotlin.concurrent.fixedRateTimer

abstract class FalconRobot {

    // Previous robot mode from previous periodic update
    var previousRobotMode = Mode.NONE
        private set

    var previousEnabledState = false
        private set

    // Last robot mode from current periodic update
    var lastRobotMode = Mode.NONE
        private set

    var lastEnabledState = false
        private set

    private fun updateState(
        newRobotMode: Mode,
        newEnabledSate: Boolean
    ) {
        this.previousRobotMode = this.lastRobotMode
        this.previousEnabledState = this.lastEnabledState
        this.lastRobotMode = newRobotMode
        this.lastEnabledState = newEnabledSate
    }

    /**
     * Executes at a specified rate
     */
    protected open fun periodic() {}

    /**
     * Executes at a specified rate but with only Networking code
     */
    protected open fun periodicNetwork() {}

    // Helpers
    protected fun addToSubsystemHandler(subsystem: FalconSubsystem) = SubsystemHandler.addSubsystem(subsystem)

    protected open operator fun FalconSubsystem.unaryPlus() = addToSubsystemHandler(this)

    enum class Mode {
        NONE,
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST;
    }

    companion object {
        fun startRobot(
            robotSource: Source<FalconRobot>,
            period: Time
        ) = RobotBase.startRobot {
            RobotBaseWrapper(robotSource, period)
        }

        private class RobotBaseWrapper(
            private val robotSource: Source<FalconRobot>,
            private val period: Time
        ) : RobotBase() {
            @Suppress("ComplexMethod")
            override fun startCompetition() {
                HAL.report(FRCNetComm.tResourceType.kResourceType_Language, kLanguageKotlin)
                LiveWindow.setEnabled(false)

                val robot = robotSource()
                SubsystemHandler.lateInit()
                println("[Robot] Initialized")

                // Tell the DS that the robot is ready to be enabled
                HAL.observeUserProgramStarting()

                // New states that need to be sent to FalconRobot
                var currentMode = Mode.NONE
                var currentEnabledState = false

                fixedRateTimer(
                    "Periodic FalconRobot Loop",
                    period = period.millisecond.toLong()
                ) {
                    val newMode = currentMode
                    val knownMode = robot.lastRobotMode
                    // Send new states
                    robot.updateState(
                        newMode,
                        currentEnabledState
                    )
                    // If new state execute Subsystem event methods
                    if (newMode != knownMode) {
                        when (newMode) {
                            Mode.AUTONOMOUS -> SubsystemHandler.autoReset()
                            Mode.TELEOP -> SubsystemHandler.teleopReset()
                            Mode.DISABLED -> SubsystemHandler.zeroOutputs()
                            else -> {
                            }
                        }
                    }
                    // Update command and subsystem loops
                    Scheduler.getInstance().run()
                    // Update robot loop
                    robot.periodic()
                }

                fixedRateTimer(
                    "Periodic FalconRobot Network Loop",
                    period = period.millisecond.toLong()
                ) {
                    // Update Values
                    SmartDashboard.updateValues()
                    Shuffleboard.update()
                    // LiveWindow.updateValues()
                    robot.periodicNetwork()
                }

                // YEET and yote to receive new data from ds
                while (true) {
                    // Wait for new data to arrive
                    m_ds.waitForData()

                    currentMode = when {
                        isDisabled -> {
                            HAL.observeUserProgramDisabled()
                            Mode.DISABLED
                        }
                        isAutonomous -> {
                            HAL.observeUserProgramAutonomous()
                            Mode.AUTONOMOUS
                        }
                        isOperatorControl -> {
                            HAL.observeUserProgramTeleop()
                            Mode.TELEOP
                        }
                        isTest -> {
                            HAL.observeUserProgramTest()
                            Mode.TEST
                        }
                        else -> TODO("Robot in invalid mode!")
                    }

                    currentEnabledState = isEnabled
                }
            }
        }
    }
}