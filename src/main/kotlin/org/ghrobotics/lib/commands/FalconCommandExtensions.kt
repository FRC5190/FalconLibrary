/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("unused")

package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.ConditionalCommand
import org.ghrobotics.lib.utils.Source

// External Extension Helpers

/**
 * Creates a command group builder [block] that will run all commands sequentially
 */
fun sequential(block: BasicCommandGroupBuilder.() -> Unit) =
    commandGroup(FalconCommandGroup.GroupType.SEQUENTIAL, block)

/**
 * Creates a command group builder [block] that will run all commands in parallel
 */
fun parallel(block: BasicCommandGroupBuilder.() -> Unit) =
    commandGroup(FalconCommandGroup.GroupType.PARALLEL, block)

private fun commandGroup(type: FalconCommandGroup.GroupType, block: BasicCommandGroupBuilder.() -> Unit) =
    BasicCommandGroupBuilder(type).apply(block).build()

/**
 * Creates a state command group builder [block] that will run commands with the matching [state]
 */
fun <T> stateCommandGroup(state: Source<T>, block: StateCommandGroupBuilder<T>.() -> Unit) =
    StateCommandGroupBuilder(state).apply(block).build()

// Builders

interface CommandGroupBuilder {
    /**
     * Creates the command group
     */
    fun build(): FalconCommandGroup
}

/**
 * Creates a [Falcon Command Group][FalconCommandGroup] with the given [type]
 */
class BasicCommandGroupBuilder(private val type: FalconCommandGroup.GroupType) :
    CommandGroupBuilder {
    private val commands = mutableListOf<FalconCommand>()

    /**
     * Adds [FalconCommand] command to the builder [BasicCommandGroupBuilder]
     */
    operator fun FalconCommand.unaryPlus() = commands.add(this)

    override fun build() = FalconCommandGroup(type, commands.map { it.wrappedValue })
}

/**
 * Creates a [Falcon State Command Group][FalconCommandGroup] with the given [state]
 */
class StateCommandGroupBuilder<T>(private val state: Source<T>) :
    CommandGroupBuilder {
    private val stateMap = mutableMapOf<T, FalconCommand>()

    /**
     * Run [block] when the state matches any [states]
     */
    fun state(vararg states: T, block: () -> FalconCommand) = states(states, block())

    /**
     * Run [command] when the state matches any [states]
     */
    fun state(vararg states: T, command: FalconCommand) = states(states, command)

    /**
     * Run [command] when the state matches any [states]
     */
    fun states(states: Array<out T>, command: FalconCommand) = states.forEach { state(it, command) }

    /**
     * Run [block] when the state is [state]
     */
    fun state(state: T, block: () -> FalconCommand) = state(state, block())

    /**
     * Run [command] when the state is [state]
     */
    fun state(state: T, command: FalconCommand) {
        if (stateMap.containsKey(state)) println("[StateCommandGroup] Warning: state $state was overwritten during building")
        stateMap[state] = command
    }

    override fun build() =
        FalconCommandGroup(FalconCommandGroup.GroupType.SEQUENTIAL,
            stateMap.entries.map { (key, command) ->
                object : ConditionalCommand(command.wrappedValue) {
                    override fun condition() = state() == key
                }
            })
}

@Suppress("FunctionName", "UNUSED_PARAMETER")
infix fun FalconCommandGroup.S3ND(other: String) = this.start()
