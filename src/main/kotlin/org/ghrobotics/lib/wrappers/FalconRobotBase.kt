package org.ghrobotics.lib.wrappers

import edu.wpi.first.hal.HAL
import edu.wpi.first.wpilibj.RobotBase
import edu.wpi.first.wpilibj.command.Scheduler
import edu.wpi.first.wpilibj.livewindow.LiveWindow
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard
import kotlinx.coroutines.isActive
import kotlinx.coroutines.runBlocking
import org.ghrobotics.lib.commands.FalconSubsystem
import org.ghrobotics.lib.commands.SubsystemHandler
import org.ghrobotics.lib.wrappers.hid.FalconHID

abstract class FalconRobotBase : RobotBase() {

    companion object {
        var DEBUG = true
            private set
        lateinit var INSTANCE: FalconRobotBase
            private set
    }

    init {
        @Suppress("LeakingThis")
        INSTANCE = this
        DEBUG = false
    }

    enum class Mode(private val any: Boolean = false) {
        NONE,
        ANY(true),
        DISABLED,
        AUTONOMOUS,
        TELEOP,
        TEST;

        val rawValues by lazy { if (any) enumValues<Mode>().toList() else listOf(this@Mode) }
    }

    var currentMode = Mode.NONE
        private set

    // State Machine
    private val onEnterListeners = mutableListOf<Pair<Mode, suspend () -> Unit>>()
    private val onLeaveListeners = mutableListOf<Pair<Mode, suspend () -> Unit>>()
    private val onTransitionListeners = mutableListOf<Pair<Pair<Mode, Mode>, suspend () -> Unit>>()
    private val onWhileListeners = mutableListOf<Pair<Mode, suspend () -> Unit>>()

    fun onEnter(enterState: Mode, listener: suspend () -> Unit) = onEnterListeners.add(enterState to listener)
    fun onLeave(leaveState: Mode, listener: suspend () -> Unit) = onLeaveListeners.add(leaveState to listener)
    fun onTransition(fromState: Mode, toState: Mode, listener: suspend () -> Unit) =
        onTransitionListeners.add((fromState to toState) to listener)

    fun onWhile(whileState: Mode, listener: suspend () -> Unit) = onWhileListeners.add(whileState to listener)

    // Main Robot Code

    var initialized = false
        private set

    abstract suspend fun initialize()

    override fun startCompetition() = runBlocking {
        LiveWindow.setEnabled(false)
        // Disabled
        onWhile(Mode.DISABLED) { HAL.observeUserProgramDisabled() }
        // Autonomous
        onWhile(Mode.AUTONOMOUS) { HAL.observeUserProgramAutonomous() }
        // TeleOp
        onWhile(Mode.TELEOP) { HAL.observeUserProgramTeleop() }
        // Test
        onEnter(Mode.TEST) { LiveWindow.setEnabled(true) }
        onWhile(Mode.TEST) { HAL.observeUserProgramTest() }
        onLeave(Mode.TEST) { LiveWindow.setEnabled(false) }
        // Update Values
        onWhile(Mode.ANY) {
            SmartDashboard.updateValues()
            //            LiveWindow.updateValues()
        }

        onEnter(Mode.AUTONOMOUS) { SubsystemHandler.autoReset() }
        onEnter(Mode.TELEOP) { SubsystemHandler.teleopReset() }
        onEnter(Mode.DISABLED) { SubsystemHandler.zeroOutputs() }

        initialize()
        SubsystemHandler.lateInit()
        initialized = true
        // Start up the default command
        println("[Robot] Initialized")
        // Update Commands
        onWhile(Mode.ANY) {
            Scheduler.getInstance().run()
        }

        // Tell the DS that the robot is ready to be enabled
        HAL.observeUserProgramStarting()

        while (isActive) {
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
                // Leave previous mode
                for ((mode, listener) in onLeaveListeners) {
                    if (mode == currentMode || mode == Mode.ANY) {
                        listener()
                    }
                }
                // Transition
                for ((modes, listener) in onTransitionListeners) {
                    if ((modes.first == currentMode || modes.first == Mode.ANY) &&
                        (modes.second == newMode || modes.second == Mode.ANY)
                    ) {
                        listener()
                    }
                }
                // Enter new mode
                for ((mode, listener) in onEnterListeners) {
                    if (mode == newMode || mode == Mode.ANY) {
                        listener()
                    }
                }
            }
            // On while
            for ((mode, listener) in onWhileListeners) {
                if (mode == newMode || mode == Mode.ANY) {
                    listener()
                }
            }
        }
    }

    // Helpers
    protected operator fun FalconSubsystem.unaryPlus() = SubsystemHandler.addSubsystem(this)

    protected suspend operator fun FalconHID<*>.unaryPlus() = onWhile(Mode.TELEOP) { update() }
}
