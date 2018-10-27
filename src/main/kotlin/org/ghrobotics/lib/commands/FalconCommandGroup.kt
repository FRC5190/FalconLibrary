package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import edu.wpi.first.wpilibj.command.CommandGroup
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.mathematics.units.second

class FalconCommandGroup(
    private val groupType: GroupType,
    private val commands: List<Command>
) : AbstractFalconCommand() {

    private val _wpiCommand = FalconWpiGroup()
    override val wpiCommand: CommandGroup = _wpiCommand

    private inner class FalconWpiGroup : CommandGroup() {
        var timeout = 0.second
            set(value) {
                setTimeout(value.second.asDouble)
                field = value
            }

        init {
            when (groupType) {
                GroupType.PARALLEL -> commands.forEach {
                    addParallel(it)
                }
                GroupType.SEQUENTIAL -> commands.forEach {
                    addSequential(it)
                }
            }
        }

        override fun isFinished() = super.isFinished() || _finishCondition.value
    }

    override fun withTimeout(delay: Time) = apply { _wpiCommand.timeout = delay }

    enum class GroupType {
        PARALLEL,
        SEQUENTIAL
    }
}