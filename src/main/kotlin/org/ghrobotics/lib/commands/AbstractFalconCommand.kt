package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.command.Command
import kotlinx.coroutines.experimental.GlobalScope
import kotlinx.coroutines.experimental.disposeOnCancellation
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.observabletype.ObservableValue
import org.ghrobotics.lib.utils.observabletype.ObservableValueReference
import org.ghrobotics.lib.utils.observabletype.or
import org.ghrobotics.lib.utils.observabletype.updatableValue
import org.ghrobotics.lib.wrappers.FalconRobotBase

abstract class AbstractFalconCommand {

    init {
        if (!FalconRobotBase.DEBUG && FalconRobotBase.INSTANCE.initialized) {
            println("[FalconCommand} [WARNING] It is not recommended to create commands after the robot has initialized!")
        }
    }

    abstract val wpiCommand: Command

    @Suppress("PropertyName")
    protected val _finishCondition = FinishCondition()
    val finishCondition: ObservableValue<Boolean> = _finishCondition

    val commandState by lazy {
        GlobalScope.updatableValue {
            when {
                wpiCommand.isRunning -> CommandState.BAKING
                wpiCommand.isCompleted -> CommandState.BAKED
                else -> CommandState.PREPARED
            }
        }
    }

    // Some methods for people who know what they are doing

    protected open suspend fun initialize0() = initialize()
    protected open suspend fun execute0() = execute()
    protected open suspend fun dispose0() = dispose()

    // Methods that don't need anything special

    protected open suspend fun initialize() {}
    protected open suspend fun execute() {}
    protected open suspend fun dispose() {}

    fun start() = wpiCommand.start()
    fun stop() = wpiCommand.cancel()

    fun withExit(condition: ObservableValue<Boolean>) = also { _finishCondition += condition }
    fun overrideExit(condition: ObservableValue<Boolean>) = also { _finishCondition.set(condition) }
    abstract fun withTimeout(delay: Time): AbstractFalconCommand

    suspend fun await() = suspendCancellableCoroutine<Unit> { cont ->
        cont.disposeOnCancellation(commandState.invokeOnceWhen(CommandState.BAKED) {
            cont.resume(Unit)
        })
    }

    protected class FinishCondition private constructor(private val varReference: ObservableValueReference<Boolean>) :
        ObservableValue<Boolean> by varReference {
        constructor() : this(ObservableValueReference(ObservableValue(false)))

        operator fun plusAssign(other: ObservableValue<Boolean>) {
            varReference.reference = varReference.reference or other
        }

        fun set(other: ObservableValue<Boolean>) {
            varReference.reference = other
        }

        override fun toString() = "FINISH($varReference)[$value]"
    }
}