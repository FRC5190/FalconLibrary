package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.CommandGroup
import org.ghrobotics.lib.mathematics.units.second
import kotlin.properties.Delegates.observable

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

        override fun isFinished() = super.isFinished() || finishCondition.value
    }

    enum class GroupType {
        PARALLEL,
        SEQUENTIAL
    }
}