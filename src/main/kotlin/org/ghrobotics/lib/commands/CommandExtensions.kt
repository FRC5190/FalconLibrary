/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("unused")

package org.ghrobotics.lib.commands

import org.ghrobotics.lib.utils.Source
import org.ghrobotics.lib.utils.observabletype.ObservableValue

// External Extension Helpers

fun sequential(block: BasicCommandGroupBuilder.() -> Unit) = sequential0(block)
fun parallel(block: BasicCommandGroupBuilder.() -> Unit) = parallel0(block)
fun <T> stateCommandGroup(state: Source<T>, block: StateCommandGroupBuilder<T>.() -> Unit) =
        StateCommandGroupBuilder(state).also { block(it) }.build()

// Internal Extension Helpers

private fun sequential0(block: BasicCommandGroupBuilder.() -> Unit) = commandGroup(CommandGroup.GroupType.SEQUENTIAL, block)
private fun parallel0(block: BasicCommandGroupBuilder.() -> Unit) = commandGroup(CommandGroup.GroupType.PARALLEL, block)
private fun <T> stateCommandGroup0(stateSource: Source<T>, block: StateCommandGroupBuilder<T>.() -> Unit) = stateCommandGroup(stateSource, block)

private fun commandGroup(type: CommandGroup.GroupType, block: BasicCommandGroupBuilder.() -> Unit) =
        BasicCommandGroupBuilder(type).also { block(it) }.build()

// Builders

interface CommandGroupBuilder {
    fun build(): CommandGroup
}

class BasicCommandGroupBuilder(private val type: CommandGroup.GroupType) : CommandGroupBuilder {
    private val commands = mutableListOf<Command>()

    operator fun Command.unaryPlus() = commands.add(this)

    override fun build() = CommandGroup(type, commands)
}

class StateCommandGroupBuilder<T>(private val state: Source<T>) : CommandGroupBuilder {
    private val stateMap = mutableMapOf<T, Command>()

    fun state(vararg states: T, block: () -> Command) = state(states = *states, command = block())
    fun state(vararg states: T, command: Command) = states.forEach { state(it, command) }

    fun state(state: T, block: () -> Command) = state(state, block())
    fun state(state: T, command: Command) {
        if (stateMap.containsKey(state)) println("[StateCommandGroup] Warning: state $state was overwritten during building")
        stateMap[state] = command
    }

    override fun build(): CommandGroup = object : CommandGroup(GroupType.SEQUENTIAL, stateMap.values.toList()) {
        override fun createTasks(): List<CommandGroupTask> {
            val currentState = state.value
            val command = stateMap[currentState]
            if (command == null) {
                println("[StateCommandGroup] Missing state: $currentState")
                return emptyList()
            }
            return listOf(CommandGroupTask(command))
        }
    }
}


@Suppress("FunctionName", "UNUSED_PARAMETER")
infix fun CommandGroup.S3ND(other: String) {
    this.start()
}
