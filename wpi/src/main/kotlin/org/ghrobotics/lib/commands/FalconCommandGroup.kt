package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.CommandGroup
import org.ghrobotics.lib.mathematics.units.second
import kotlin.properties.Delegates.observable

/**
 *  Kotlin Wrapper for [WPI's Command Group][CommandGroup]
 *  @param groupType the type of command group
 *  @param commands the commands the group will run
 */
class FalconCommandGroup(
    private val groupType: GroupType,
    private val commands: List<Command>
) : FalconCommand() {

    override val wrappedValue: CommandGroup = WpiCommandGroup()

    private inner class WpiCommandGroup : CommandGroup(), IWpiCommand {
        override var timeout by observable(0.second) { _, _, newValue ->
            setTimeout(newValue.second)
        }

        init {
            when (groupType) {
                GroupType.PARALLEL -> commands.forEach { addParallel(it) }
                GroupType.SEQUENTIAL -> commands.forEach { addSequential(it) }
            }
        }

        override fun isFinished() = super.isTimedOut() || super.isFinished() || finishCondition()
    }

    enum class GroupType {
        /**
         * All Commands will run in Parallel
         */
        PARALLEL,
        /**
         * All Commands will run Sequentially
         */
        SEQUENTIAL
    }
}