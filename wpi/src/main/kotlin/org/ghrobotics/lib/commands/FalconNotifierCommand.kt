package org.ghrobotics.lib.commands

import edu.wpi.first.wpilibj.experimental.command.SendableCommandBase
import edu.wpi.first.wpilibj.experimental.command.Subsystem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import org.ghrobotics.lib.mathematics.units.Time
import org.ghrobotics.lib.utils.launchFrequency

open class FalconNotifierCommand(
    private val period: Time,
    vararg requirements: Subsystem,
    private val block: suspend CoroutineScope.() -> Unit
) : SendableCommandBase() {

    init {
        addRequirements(*requirements)
    }

    lateinit var job: Job

    override fun initialize() {
        job = GlobalScope.launchFrequency((1 / period.value).toInt(), block = block)
    }

    override fun end(interrupted: Boolean) {
        job.cancel()
    }
}