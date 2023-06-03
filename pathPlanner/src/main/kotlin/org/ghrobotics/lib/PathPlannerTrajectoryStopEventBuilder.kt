/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib

import com.pathplanner.lib.PathPlannerTrajectory
import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.Commands
import edu.wpi.first.wpilibj2.command.FunctionalCommand

/**
 * Private code from Path Planner we needed to extract
 */
object PathPlannerTrajectoryStopEventBuilder {
    fun stopEventGroup(stopEvent: com.pathplanner.lib.PathPlannerTrajectory.StopEvent, eventMap: HashMap<String, Command>): CommandBase {
        if (stopEvent.names.isEmpty()) {
            return Commands.waitSeconds(stopEvent.waitTime)
        }

        val eventCommands: CommandBase = stopEventCommands(stopEvent, eventMap)

        return when (stopEvent.waitBehavior) {
            com.pathplanner.lib.PathPlannerTrajectory.StopEvent.WaitBehavior.BEFORE -> Commands.sequence(
                Commands.waitSeconds(stopEvent.waitTime),
                eventCommands,
            )
            com.pathplanner.lib.PathPlannerTrajectory.StopEvent.WaitBehavior.AFTER -> Commands.sequence(
                eventCommands,
                Commands.waitSeconds(stopEvent.waitTime),
            )
            com.pathplanner.lib.PathPlannerTrajectory.StopEvent.WaitBehavior.DEADLINE -> Commands.deadline(
                Commands.waitSeconds(stopEvent.waitTime),
                eventCommands,
            )
            com.pathplanner.lib.PathPlannerTrajectory.StopEvent.WaitBehavior.MINIMUM -> Commands.parallel(
                Commands.waitSeconds(stopEvent.waitTime),
                eventCommands,
            )
            com.pathplanner.lib.PathPlannerTrajectory.StopEvent.WaitBehavior.NONE -> eventCommands
            else -> eventCommands
        }
    }

    fun stopEventCommands(stopEvent: PathPlannerTrajectory.StopEvent, eventMap: HashMap<String, Command>): CommandBase {
        val commands = mutableListOf<CommandBase>()
        val startIndex = if (stopEvent.executionBehavior == PathPlannerTrajectory.StopEvent.ExecutionBehavior.PARALLEL_DEADLINE) 1 else 0
        for (i in startIndex until stopEvent.names.size) {
            val name = stopEvent.names[i]
            if (eventMap.containsKey(name)) {
                commands.add(wrappedEventCommand(eventMap[name]!!))
            }
        }

        return when (stopEvent.executionBehavior) {
            PathPlannerTrajectory.StopEvent.ExecutionBehavior.SEQUENTIAL -> Commands.sequence(*commands.toTypedArray())

            PathPlannerTrajectory.StopEvent.ExecutionBehavior.PARALLEL -> Commands.parallel(*commands.toTypedArray())

            PathPlannerTrajectory.StopEvent.ExecutionBehavior.PARALLEL_DEADLINE -> {
                val deadline: Command =
                    if (eventMap.containsKey(stopEvent.names[0])) {
                        wrappedEventCommand(
                            eventMap[stopEvent.names[0]]!!,
                        )
                    } else Commands.none()
                Commands.deadline(deadline, *commands.toTypedArray())
            }

            else -> throw java.lang.IllegalArgumentException(
                "Invalid stop event execution behavior: " + stopEvent.executionBehavior,
            )
        }
    }

    private fun wrappedEventCommand(eventCommand: Command): CommandBase {
        return FunctionalCommand(
            { eventCommand.initialize() },
            { eventCommand.execute() },
            { interrupted: Boolean? -> eventCommand.end(interrupted!!) },
            { eventCommand.isFinished },
            *eventCommand.requirements.toTypedArray(),
        )
    }
}
