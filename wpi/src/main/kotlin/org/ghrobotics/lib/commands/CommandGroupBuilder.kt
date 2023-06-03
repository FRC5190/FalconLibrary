/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 *
 * Copyright 2019, Green Hope Falcons
 */

package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj2.command.Command
import edu.wpi.first.wpilibj2.command.CommandBase
import edu.wpi.first.wpilibj2.command.ConditionalCommand
import edu.wpi.first.wpilibj2.command.InstantCommand
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup
import edu.wpi.first.wpilibj2.command.ParallelRaceGroup
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup
import org.ghrobotics.lib.utils.Source
import java.util.function.BooleanSupplier

fun sequential(block: BasicCommandGroupBuilder.() -> Unit) =
    commandGroup(BasicCommandGroupBuilder.Type.Sequential, block)

fun parallel(block: BasicCommandGroupBuilder.() -> Unit) =
    commandGroup(BasicCommandGroupBuilder.Type.Parallel, block)

fun parallelRace(block: BasicCommandGroupBuilder.() -> Unit) =
    commandGroup(BasicCommandGroupBuilder.Type.ParallelRace, block)

fun parallelDeadline(deadline: Command, block: ParallelDeadlineGroupBuilder.() -> Unit) =
    parallelDeadlineGroup(deadline, block)

fun <T> stateCommandGroup(state: Source<T>, block: StateCommandGroupBuilder<T>.() -> Unit) =
    StateCommandGroupBuilder(state).apply(block).build()

private fun commandGroup(type: BasicCommandGroupBuilder.Type, block: BasicCommandGroupBuilder.() -> Unit) =
    BasicCommandGroupBuilder(type).apply(block).build()

private fun parallelDeadlineGroup(deadline: Command, block: ParallelDeadlineGroupBuilder.() -> Unit) =
    ParallelDeadlineGroupBuilder(deadline).apply(block).build()

interface CommandGroupBuilder {
    fun build(): CommandBase
}

class BasicCommandGroupBuilder(private val type: Type) : CommandGroupBuilder {

    private val commands = mutableListOf<Command>()

    operator fun Command.unaryPlus() = commands.add(this)

    override fun build(): CommandBase {
        return when (type) {
            Type.Sequential -> SequentialCommandGroup(*commands.toTypedArray())
            Type.Parallel -> ParallelCommandGroup(*commands.toTypedArray())
            Type.ParallelRace -> ParallelRaceGroup(*commands.toTypedArray())
        }
    }

    enum class Type {
        Sequential, Parallel, ParallelRace
    }
}

class ParallelDeadlineGroupBuilder(private val deadline: Command) : CommandGroupBuilder {

    private val commands = mutableListOf<Command>()

    operator fun Command.unaryPlus() = commands.add(this)

    override fun build() = ParallelDeadlineGroup(deadline, *commands.toTypedArray())
}

class StateCommandGroupBuilder<T>(private val state: Source<T>) :
    CommandGroupBuilder {
    private val stateMap = mutableMapOf<T, Command>()

    fun state(vararg states: T, block: () -> Command) = states(states, block())
    fun state(vararg states: T, command: Command) = states(states, command)

    fun states(states: Array<out T>, command: Command) = states.forEach { state(it, command) }

    fun state(state: T, block: () -> Command) = state(state, block())

    fun state(state: T, command: Command) {
        if (stateMap.containsKey(state)) println("[StateCommandGroup] Warning: state $state was overwritten during building")
        stateMap[state] = command
    }

    override fun build() =
        SequentialCommandGroup(
            *stateMap.entries.map { (key, command) ->
                ConditionalCommand(command, InstantCommand(), BooleanSupplier { state() == key })
            }.toTypedArray(),
        )
}

infix fun CommandBase.S3ND(other: Any) = this.schedule()
