/*
 * FRC Team 5190
 * Green Hope Falcons
 */

@file:Suppress("unused")

package frc.team5190.lib.extensions

import frc.team5190.lib.commands.Command
import frc.team5190.lib.commands.CommandGroup
import frc.team5190.lib.commands.ParallelCommandGroup
import frc.team5190.lib.commands.SequentialCommandGroup
import frc.team5190.lib.utils.Source
import frc.team5190.lib.utils.State

// External Extension Helpers

fun sequential(block: BasicCommandGroupBuilder.() -> Unit) = sequential0(block)
fun parallel(block: BasicCommandGroupBuilder.() -> Unit) = parallel0(block)
fun <T> stateCommandGroup(state: Source<T>, block: StateCommandGroupBuilder<T>.() -> Unit) = StateCommandGroupBuilder(state).also { block(it) }.build()

// Internal Extension Helpers

private fun sequential0(block: BasicCommandGroupBuilder.() -> Unit) = commandGroup(BasicCommandGroupBuilder.BuilderType.SEQUENTIAL, block)
private fun parallel0(block: BasicCommandGroupBuilder.() -> Unit) = commandGroup(BasicCommandGroupBuilder.BuilderType.PARALLEL, block)
private fun <T> stateCommandGroup0(state: State<T>, block: StateCommandGroupBuilder<T>.() -> Unit) = stateCommandGroup(state, block)

private fun commandGroup(type: BasicCommandGroupBuilder.BuilderType, block: BasicCommandGroupBuilder.() -> Unit) = BasicCommandGroupBuilder(type).also { block(it) }.build()

// Builders

interface CommandGroupBuilder {
    fun build(): CommandGroup
}

class BasicCommandGroupBuilder(val type: BuilderType) : CommandGroupBuilder {
    enum class BuilderType { SEQUENTIAL, PARALLEL }

    private val commands = mutableListOf<Command>()

    fun sequential(block: CommandGroupBuilder.() -> Unit) = sequential0(block).also { it.unaryPlus() }
    fun parallel(block: CommandGroupBuilder.() -> Unit) = parallel0(block).also { it.unaryPlus() }
    fun <T> stateCommandGroup(state: State<T>, block: StateCommandGroupBuilder<T>.() -> Unit) = stateCommandGroup0(state, block).also { it.unaryPlus() }

    operator fun Command.unaryPlus() = commands.add(this)

    override fun build() = when (type) {
        BuilderType.SEQUENTIAL -> SequentialCommandGroup(commands)
        BuilderType.PARALLEL -> ParallelCommandGroup(commands)
    }
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

    override fun build(): CommandGroup = object : SequentialCommandGroup(stateMap.values.toList()) {
        override fun initTasks(): List<GroupCommandTask> {
            val currentState = state.value
            val command = stateMap[currentState]
            if (command == null) {
                println("[StateCommandGroup] Missing state: $currentState")
                return emptyList()
            }
            return listOf(GroupCommandTask(this, command))
        }
    }
}


@Suppress("FunctionName", "UNUSED_PARAMETER")
infix fun CommandGroup.S3ND(other: String) {
    this.start()
}
